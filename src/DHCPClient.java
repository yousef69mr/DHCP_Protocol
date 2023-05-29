import java.io.IOException;
import java.net.*;
import java.util.Random;

public class DHCPClient
{
    public static String randomMACAddress()
    {
        Random rand = new Random();
        byte[] macAddr = new byte[6];
        rand.nextBytes(macAddr);

        macAddr[0] = (byte)(macAddr[0] & (byte)254);  //zeroing last 2 bytes to make it unicast and locally adminstrated

        StringBuilder sb = new StringBuilder(18);
        for(byte b : macAddr){

            if(sb.length() > 0)
                sb.append(":");

            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static void main(String[] args)
    {
        try {
            DatagramSocket clientSocket = new DatagramSocket();

            InetAddress serverIP = InetAddress.getByName("localhost");
            int serverPort = 5000;
            byte[] requestBytes;
            byte [] responseFromServer =new byte[4096] ;
            byte [] DHCPREQUESTmsg;
            byte [] DHCPacknowledgement = new byte[4096];
            String mac = randomMACAddress() ;
            byte[] macBytes;
            macBytes = mac.getBytes();
            System.out.println(mac);



            // sending the server the DHCPdiscover message
            String DHCPDISCOVER = "DHCPDISCOVER:source MAC address is : " +mac + ", destination MAC address is : 255.255.255.255  ";
            requestBytes = DHCPDISCOVER.getBytes();
            InetAddress broadcast = InetAddress.getByName("255.255.255.255");
            DatagramPacket myClientPacket = new DatagramPacket(requestBytes, requestBytes.length, broadcast , serverPort);
            clientSocket.send(myClientPacket);


            // sending the mac address
            DatagramPacket MacPacket = new DatagramPacket(macBytes,macBytes.length,broadcast,serverPort);
            clientSocket.send(MacPacket);

            //recieving the DHCPoffermsg
            DatagramPacket serverPacket = new DatagramPacket(responseFromServer, responseFromServer.length);
            clientSocket.receive(serverPacket);
            String DHCPoffermsg = new String(serverPacket.getData()).trim();


            //sending the DHCPREQUESTmsg
            String DHCPrequest = "I request IP address offered"+ "DHCP server IP"+serverIP ;
            DHCPREQUESTmsg = DHCPrequest.getBytes();
            DatagramPacket myClientPacket2 = new DatagramPacket(DHCPREQUESTmsg, DHCPREQUESTmsg.length, serverIP , serverPort);
            clientSocket.send(myClientPacket2);


            //Reciving the DHCPacknowledgementmsg
            DatagramPacket DHCPacknowledgementmsg = new DatagramPacket(responseFromServer, responseFromServer.length);
            clientSocket.receive(DHCPacknowledgementmsg);
            String DHCPackmsg = new String(serverPacket.getData()).trim();

            String str = DHCPacknowledgementmsg.toString();

            System.out.print(str);
        }
        catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
