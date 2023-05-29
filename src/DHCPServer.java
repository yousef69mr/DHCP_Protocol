//import javafx.util.Pair;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

public class DHCPServer
{


    public static <bool> void main(String[] args) {
        {
            try {
                DatagramSocket serverSocket;
                serverSocket = new DatagramSocket(5000);
                System.out.println("Server is up");

                byte[] requestBytes = new byte[4096];
                byte[] responseBytes;
                byte[] DHCPrequest = new byte[4096];
                byte [] DHCPacknowledgement ;
                byte[] macBytes = new byte[20];
                Scanner scan = new Scanner(System.in);

                // making a bool of ip addresses
                InetAddress serverIP = InetAddress.getByName("localhost"); // 127.0.0.1
                ArrayList<String> AvailbleIPs = new ArrayList<String>();
                AvailbleIPs.add("127.0.0.2");
                AvailbleIPs.add("127.0.0.3");
                AvailbleIPs.add("127.0.0.4");
                AvailbleIPs.add("127.0.0.5");
                AvailbleIPs.add("127.0.0.6");
                //ArrayList<Pair> AssociatedIPs  = new ArrayList<Pair>();
                ArrayList<String>ReservedIPs= new ArrayList<String>();

                //recive the clientDHCPdiscover packet
                DatagramPacket clientPacket = new DatagramPacket(requestBytes, requestBytes.length);
                serverSocket.receive(clientPacket);
                String clientDHCPdiscover = new String(clientPacket.getData()).trim();


                // Reciving the MacAddress
                DatagramPacket MacPacket = new DatagramPacket(macBytes,macBytes.length);
                serverSocket.receive(MacPacket);
                String c_mac = new String(MacPacket.getData()).trim();

                System.out.println(c_mac);

                // ip addresses for broadcast, subnetMaskIP,RouterIP and DNS
                InetAddress broadcast = InetAddress.getByName("255.255.255.255");
                String subnetMaskIP = "255.255.255.0";
                String RouterIP = "192.168.1.6";
                String DNsIP1= "8.8.8.8";
                String DNsIP2= "8.8.4.4";
                //NetworkInterface network = NetworkInterface.getByInetAddress(serverIP);
                String LeaseTime = "00:02:00";

                //sending the DHCPOFFERmsg messange
                int clientPort = clientPacket.getPort();
                String DHCPOFFERmsg = "Your IP Address can be:" + AvailbleIPs.get(0) + "Server IP address is: "+serverIP
                        + "Router IP address: " + RouterIP + "Subnet mask" + subnetMaskIP + "IP address lease time is: "
                        + LeaseTime + "DNS Servers are" + DNsIP1 + ", " +DNsIP2;
                responseBytes = DHCPOFFERmsg.getBytes();
                DatagramPacket MyServerPacket = new DatagramPacket(responseBytes, responseBytes.length, broadcast, clientPort);
                serverSocket.send(MyServerPacket);

                //pair the ip address and the mac address
                String OfferedIP = AvailbleIPs.get(0);
                /*Pair Offered = new Pair(AvailbleIPs.get(0), c_mac);
                System.out.println(Offered);*/

                //recieving the DHCPrequestmsg
                DatagramPacket clientPacket2 = new DatagramPacket(DHCPrequest, DHCPrequest.length);
                serverSocket.receive(clientPacket2);
                String DHCPrequestmsg = new String(clientPacket2.getData()).trim();

               // AssociatedIPs.add(Offered);
                ReservedIPs.add(AvailbleIPs.get(0));
                AvailbleIPs.remove(0);


                //time
                Boolean LeaseTimeExpired= false;
                final long ONE_MINUTE_IN_MILLIS=60000;
                final long Lease_time_In_Millis =120000 ;
                Calendar AfterAssIP = Calendar.getInstance();
                long AfterAssIPinMilli = AfterAssIP.getTimeInMillis();
                Date afterAddingleaseTime=new Date(AfterAssIPinMilli + Lease_time_In_Millis);
                Date dateUpadted = new Date();
                if(afterAddingleaseTime==dateUpadted)
                {
                    AvailbleIPs.add(ReservedIPs.get(0));
                   // AssociatedIPs.remove(0);
                    //sending the client a msg
                    LeaseTimeExpired = true;
                    String LeaseTimeExpiredstr = LeaseTimeExpired.toString();



                }


                //sending the DHCPacknowledgement
                String DHCPacknowledgementmsg = "Your IP address is" /*AssociatedIPs.get(0)*/+ "Srever IP is:" +serverIP + "Router IP is"
                        + RouterIP+ "Subnet mask IP:"+ subnetMaskIP +"IP address lease time is"+ LeaseTime + "DNS Servers are: " +DNsIP1 + ", " + DNsIP2;
                DHCPacknowledgement = DHCPacknowledgementmsg.getBytes();
                DatagramPacket DHCPack = new DatagramPacket(DHCPacknowledgement, DHCPacknowledgement.length, serverIP, clientPort);
                serverSocket.send(DHCPack);



            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
