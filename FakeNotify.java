import java.util.LinkedList;
import java.util.Queue;

public class FakeNotify {
    private static Queue<Integer> queue = new LinkedList<>();
    private static final int MAX_SIZE = 10;
    /**
     * 生产元素加入队列
     * @param ele
     */
    public void produce(Integer ele){
        synchronized (queue){
            //消息队列满，则等待队列空闲
            while (queue.size() == MAX_SIZE){
                try {
                    //挂起当前线程，等待消费者线程消费队列
                    queue.wait();
                    System.out.println("消费队列已满");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //空闲则生成元素，通知消费者线程
            queue.add(ele);
            System.out.println("生产元素加入队列: " + ele);
            queue.notifyAll();
        }
    }

    /**
     * 消费队列元素
     */
    public void consume(){
        synchronized (queue){
            //消费队列为空
            while(queue.size() == 0){
                try {
                    //挂起当前线程，等待生产者线程将
                    queue.wait();
                    System.out.println("消费队列已空");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            int res = queue.poll();
            System.out.println("消费队列元素: " + res);
            queue.notifyAll();
        }
    }

    public static void main(String[] args) {
        for(int i = 0;i<20;i++){
            final int tmp = i;
            new Thread(() -> {
                new FakeNotify().produce(tmp);
            }).start();
        }
        for(int i = 0;i<20;i++){
            new Thread(() -> {
                new FakeNotify().consume();
            }).start();
        }
    }


}
