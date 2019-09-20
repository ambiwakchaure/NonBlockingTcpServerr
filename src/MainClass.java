import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class MainClass
{

    public static void main(String[] args) throws Exception
    {
        System.out.println("VTS TCP v1.0 (GT06, AIS140, TS101) Running...");

        InetAddress host = InetAddress.getByName(Constants.SERVER_IP);
        Selector selector = Selector.open();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(host, Constants.SERVER_PORT));
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        SelectionKey key = null;
        SocketChannel client = null;
        Socket clientSocket = null;

        while (true)
        {
            try
            {
                if (selector.select() <= 0)
                    continue;
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectedKeys.iterator();
                while (iterator.hasNext())
                {
                    key = (SelectionKey) iterator.next();
                    iterator.remove();
                    if (key.isAcceptable())
                    {
                        SocketChannel sc = serverSocketChannel.accept();
                        sc.configureBlocking(false);
                        sc.register(selector, SelectionKey.OP_READ);
                        //System.out.println("Connection Accepted: " + sc.getLocalAddress());
                        System.out.println("Device connected");
                    }
                    String hexString = "";
                    if (key.isReadable())
                    {
                        SocketChannel sc = (SocketChannel) key.channel();
                        ByteBuffer bb = ByteBuffer.allocate(15000);
                        sc.read(bb);

                        hexString = new String(bb.array(),"ISO_8859_1").trim();
                        //hexString = new String(bb.array(),  "ISO_8859_1");
                        if(!(hexString.isEmpty() || hexString == null))
                        {
                            System.out.println("-----------------------------------------------------------------");
                            System.out.println("Data : "+ hexString);
                            //get client socket
                            client = serverSocketChannel.accept();
                            clientSocket = client.socket();

                            if (hexString.contains("$,5,"))
                            {
                                String[] commandData = hexString.split("#");
                                if (commandData.length == 2)
                                {
                                    String command = commandData[0]+"#";
                                    String imei = commandData[1];
                                    String webimei = imei+"web";
                                    T.storeCommandSockets(webimei,clientSocket);
                                    T.writeCommand(clientSocket, imei, command);
                                }
                            }
                            else if (hexString.contains("$,6,"))
                            {
                                if(T.CLIENT_SOCKETS_CMD.containsValue(clientSocket))
                                {
                                    for (Map.Entry<String, Socket> entry : T.CLIENT_SOCKETS_CMD.entrySet())
                                    {
                                        if (Objects.equals(clientSocket, entry.getValue()))
                                        {
                                            Socket webSocket = T.CLIENT_SOCKETS_CMD.get(entry.getKey()+"web");
                                            String keyImei = entry.getKey();
                                            T.writeMessage(keyImei,webSocket, hexString);
                                            LogMaster.writeCommandLog(webSocket,keyImei, hexString, "1");
                                            System.out.println("Command write successfully IMEI : " + keyImei);
                                            webSocket.close();
                                            T.CLIENT_SOCKETS_CMD.remove(entry.getKey()+"web");
                                            break;
                                        }
                                    }
                                }
                            }
                            else
                            {
                                //TS101
                                if(hexString.contains("TS_101"))
                                {
                                    ExceuteProtocol.executeProtocolTS101(hexString,clientSocket);
                                }
                                //AIS140
                                else if(hexString.contains(","))
                                {
                                    ExceuteProtocol.executeProtocolAIS140(hexString,clientSocket);
                                }
                                //GT06
                                else
                                {
                                    ExceuteProtocol.executeProtocolGT06(hexString,clientSocket);
                                }
                            }
                        }

                        if (hexString.length() <= 0)
                        {
                            sc.close();
                            System.out.println("socket close from client");
                            //remove socket data when socket close
                            String keyData = clientSocket.getRemoteSocketAddress().toString().replace("/", "");
                            SocketInfo info = T.CLIENT_SOCKETS.get(keyData);
                            //update device status
                            String imei = info.getDeviceId();
                            if(imei != null)
                            {
                                T.updateDeviceStatus(info.getDeviceId(), "0");
                            }
                            T.CLIENT_SOCKETS.remove(keyData);
                            LogMaster.saveOpenCloseDevie(clientSocket.getRemoteSocketAddress().toString().replace("/", ""), "close");
                            //save device count
                            LogMaster.clearFile();
                            LogMaster.deviceStat("" + T.CLIENT_SOCKETS.size());
                            clientSocket.close();
                            LogMaster.saveDeviceDetails(
                                    "Disconected",
                                    "NA",
                                    String.valueOf(clientSocket.getPort()),
                                    String.valueOf(clientSocket.getLocalPort()),
                                    String.valueOf(clientSocket.getRemoteSocketAddress().toString().replace("/", "")),
                                    "Packet Name: not found,Exception : bytes == -1");
                            break;
                        }
                    }
                }
            }
            catch (IOException e)
            {
                String keyData = clientSocket.getRemoteSocketAddress().toString().replace("/", "");
                SocketInfo info = T.CLIENT_SOCKETS.get(keyData);
                //update device status

                T.CLIENT_SOCKETS.remove(keyData);
                System.out.println("socket close from server");
                try {
                    //save device count
                    LogMaster.clearFile();
                    LogMaster.deviceStat("" + T.CLIENT_SOCKETS.size());
                    LogMaster.saveOpenCloseDevie(clientSocket.getRemoteSocketAddress().toString().replace("/", ""), "close");
                } catch (IOException ex) {

                }
                break;
            }
            catch (Exception e)
            {
                LogMaster.saveExceptionLogDetails("MainClass 172", "" + e);
            }

        }
    }

}
