import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;

public class NonblockingServer
{

    public static void main(String[] args) throws Exception
    {
        System.out.println("Server is running...");
        InetAddress host = InetAddress.getByName("192.168.0.8");
        Selector selector = Selector.open();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(host, 9999));
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        SelectionKey key = null;


        while (true)
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
                    System.out.println("Connection Accepted: " + sc.getLocalAddress());
                }
                String hexString = "";
                if (key.isReadable())
                {
                    SocketChannel sc = (SocketChannel) key.channel();
                    ByteBuffer bb = ByteBuffer.allocate(15000);
                    sc.read(bb);
                    //String hexString = new String(bb.array()).trim();
                    hexString = new String(bb.array(),  "ISO_8859_1");
                    System.out.println("Data : "+ hexString);



                    if (hexString.length() <= 0)
                    {
                        sc.close();
                        System.out.println("Connection closed...");
                        System.out.println(
                                "Server will keep running. " +
                                        "Try running another client to " +
                                        "re-establish connection");
                    }
                }
            }
        }
    }
}
