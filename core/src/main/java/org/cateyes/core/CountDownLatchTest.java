package org.cateyes.core;

import java.util.concurrent.BrokenBarrierException;
//import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class WalkTarget  
{  
    private final int mCount = 5;  
    private final CyclicBarrier mBarrier;  
    ExecutorService mExecutor;  
  
    class BarrierAction implements Runnable  
    {  
        @Override  
        public void run()  
        {  
            // TODO Auto-generated method stub   
            System.out.println("所有线程都已经完成任务,计数达到预设值");  
            //mBarrier.reset();//恢复到初始化状态          
              
        }  
    }  
  
    WalkTarget()  
    {  
        //初始化CyclicBarrier   
        mBarrier = new CyclicBarrier(mCount, new BarrierAction());  
        mExecutor = Executors.newFixedThreadPool(mCount);  
  
        for (int i = 0; i < mCount; i++)  
        {  
            //启动工作线程   
            mExecutor.execute(new Walker(mBarrier, i));  
        }  
    }  
}  
  
//工作线程   
class Walker implements Runnable  
{  
    private final CyclicBarrier mBarrier;  
    private final int mThreadIndex;  
  
    Walker(final CyclicBarrier barrier, final int threadIndex)  
    {  
        mBarrier = barrier;  
        mThreadIndex = threadIndex;  
    }  
  
    @Override  
    public void run()  
    {  
        // TODO Auto-generated method stub   
        System.out.println("Thread " + mThreadIndex + " is running...");  
        // 执行任务   
        try  
        {  
            TimeUnit.MILLISECONDS.sleep(5000);  
            // do task   
        }  
        catch (InterruptedException e)  
        {  
            // TODO Auto-generated catch block   
            e.printStackTrace();  
        }  
  
        // 完成任务以后，等待其他线程完成任务   
        try  
        {  
            mBarrier.await();  
        }  
        catch (InterruptedException e)  
        {  
            // TODO Auto-generated catch block   
            e.printStackTrace();  
        }  
        catch (BrokenBarrierException e)  
        {  
            // TODO Auto-generated catch block   
            e.printStackTrace();  
        }  
        // 其他线程任务都完成以后，阻塞解除，可以继续接下来的任务   
        System.out.println("Thread " + mThreadIndex + " do something else");  
    }  
  
}  
  
public class CountDownLatchTest  
{  
    public static void main(String[] args)  
    {  
        // TODO Auto-generated method stub   
        //new CountDownLatchDriver2().main();   
        new WalkTarget();  
    }  
  
}  

//class Driver  
//{  
//    private static final int TOTAL_THREADS = 10;  
//    private final CountDownLatch mStartSignal = new CountDownLatch(1);  
//    private final CountDownLatch mDoneSignal = new CountDownLatch(TOTAL_THREADS);  
//  
//    void main()  
//    {  
//        for (int i = 0; i < TOTAL_THREADS; i++)  
//        {  
//            new Thread(new Worker(mStartSignal, mDoneSignal, i)).start();  
//        }  
//        System.out.println("Main Thread Now:" + System.currentTimeMillis());  
//        doPrepareWork();// 准备工作   
//        mStartSignal.countDown();// 计数减一为0，工作线程真正启动具体操作   
//        doSomethingElse();//做点自己的事情   
//        try  
//        {  
//            mDoneSignal.await();// 等待所有工作线程结束   
//        }  
//        catch (InterruptedException e)  
//        {  
//            // TODO Auto-generated catch block   
//            e.printStackTrace();  
//        }  
//        System.out.println("All workers have finished now.");  
//        System.out.println("Main Thread Now:" + System.currentTimeMillis());  
//    }  
//  
//    void doPrepareWork()  
//    {  
//        System.out.println("Ready,GO!");  
//    }  
//  
//    void doSomethingElse()  
//    {  
//        for (int i = 0; i < 100000; i++)  
//        {  
//            ;// delay   
//        }  
//        System.out.println("Main Thread Do something else.");  
//    }  
//}  
//  
//class Worker implements Runnable  
//{  
//    private final CountDownLatch mStartSignal;  
//    private final CountDownLatch mDoneSignal;  
//    private final int mThreadIndex;  
//  
//    Worker(final CountDownLatch startSignal, final CountDownLatch doneSignal,  
//            final int threadIndex)  
//    {  
//        this.mDoneSignal = doneSignal;  
//        this.mStartSignal = startSignal;  
//        this.mThreadIndex = threadIndex;  
//    }  
//  
//    @Override
//    public void run()  
//    {  
//        // TODO Auto-generated method stub   
//        try  
//        {  
//            mStartSignal.await();// 阻塞，等待mStartSignal计数为0运行后面的代码   
//                                    // 所有的工作线程都在等待同一个启动的命令   
//            doWork();// 具体操作   
//            System.out.println("Thread " + mThreadIndex + " Done Now:"  
//                    + System.currentTimeMillis());  
//            mDoneSignal.countDown();// 完成以后计数减一   
//        }  
//        catch (InterruptedException e)  
//        {  
//            // TODO Auto-generated catch block   
//            e.printStackTrace();  
//        }  
//    }  
//  
//    public void doWork()  
//    {  
//        for (int i = 0; i < 1000000; i++)  
//        {  
//            ;// 耗时操作   
//        }  
//        System.out.println("Thread " + mThreadIndex + ":do work");  
//    }  
//}  
//  
//public class CountDownLatchTest  
//{  
//    public static void main(String[] args)  
//    {  
//        // TODO Auto-generated method stub   
//        new Driver().main();  
//    }  
//  
//}  