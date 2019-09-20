
import java.util.Timer;
import java.util.TimerTask;

public class CloserThread {

    static TimerTask mTimerTaskClose;
    static Timer t;

    public static void closeDeadSockets() {
        t = new Timer("Closer Thread");
        mTimerTaskClose = new TimerTask() {
            public void run() {
                /*if (T.CLIENT_SOCKETS.isEmpty()) {
                    if (mTimerTaskClose != null) {
                        System.out.println("Closer stoped...");
                        mTimerTaskClose.cancel();
                        t.cancel();
                    }
                }*/

                //System.out.println("Closer Thread Running...");
                System.out.println("CLIENT_SOCKETS.size() : "+T.CLIENT_SOCKETS.size()+ " TimeStamp : "+T.getSystemDateTime());
                System.out.println(T.CLIENT_SOCKETS);
                if (!T.CLIENT_SOCKETS.isEmpty())
                {

                    //do code here
                    for (String key : T.CLIENT_SOCKETS.keySet()) {

                        SocketInfo info = T.CLIENT_SOCKETS.get(key);
                        String[] data = T.getSystemDateTime().split(" ");

                        long systemTimeSeconds = T.returnsTimeToSec(data[1]);
                        long storeTimeSeconds = T.returnsTimeToSec(info.getDateTime());

                        long compareTime = storeTimeSeconds + 300;//change time to disconnect 5 min
                        //long compareTime = storeTimeSeconds + 15;//change time to disconnect 1 min

                        if (compareTime < systemTimeSeconds) {
                            try {
                                LogMaster.saveOpenCloseDevie(info.getClientSocket().getRemoteAddress().toString().replace("/", ""), "close");
                                LogMaster.saveDeviceDetails(
                                        "Disconnect",
                                        info.getDeviceId(),
                                        String.valueOf(info.getClientSocket().getRemoteAddress().toString().replace("/", "")),
                                        "Packet not found : socket close by server");
                                T.updateDeviceStatus(info.getDeviceId(), "0");
                                info.getClientSocket().close();
                                T.CLIENT_SOCKETS.remove(key);
                                //save device count
                                LogMaster.clearFile();
                                LogMaster.deviceStat("" + T.CLIENT_SOCKETS.size());

                            } catch (Exception e) {

                                LogMaster.saveExceptionLogDetails("CloserThread 54", "" + e);
                                System.out.println("Exception : " + e);
                            }
                        }
                    }
                }
            }
        };
        // public void schedule (TimerTask task, long delay, long period)
        t.schedule(mTimerTaskClose, 5000, 5000);  //
    }
}
