package org.hometask.transactionmanagement.performace;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.URLUtil;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.time.StopWatch;
import org.hometask.transactionmanagement.application.dto.TransactionCreateDTO;
import org.hometask.transactionmanagement.application.dto.TransactionUpdateDTO;
import org.hometask.transactionmanagement.application.vo.TransactionVO;
import org.hometask.transactionmanagement.utility.model.R;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TransactionApiLoadTest {

    @LocalServerPort
    private int port;

    @Resource
    private TestRestTemplate restTemplate;

    private final String baseUrl = "http://localhost:";


    @Test
    public void loadTest_createTransaction() {
        int threadNumber = 100;
        int threadPerRequestCount = 10;

        // 同步器用以控制线程结束
        CountDownLatch countDownLatch = new CountDownLatch(threadNumber);
        // 统计成功和失败的次数
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        StopWatch stopwatch = StopWatch.createStarted();
        for (int i = 0; i < threadNumber; i++) {
            new Thread(() -> {
                try {
                    for (int j = 0; j < threadPerRequestCount; j++) {
                        TransactionCreateDTO transactionCreateDTO = new TransactionCreateDTO()
                                .setTransactionCode("test_" + RandomUtil.randomString(4))
                                .setAmount(BigDecimal.valueOf(1000))
                                .setType(1)
                                .setStatus("1");
                        ResponseEntity<R> response = restTemplate.postForEntity(
                                baseUrl + port + "/api/v1/transaction",
                                transactionCreateDTO,
                                R.class
                        );
                        HttpStatusCode status = response.getStatusCode();
                        if (status.is2xxSuccessful()) {
                            successCount.incrementAndGet();
                        } else {
                            failCount.incrementAndGet();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    // 线程结束，同步器减一
                    countDownLatch.countDown();
                }
            }).start();
        }

        // 等待所有线程结束
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        stopwatch.stop();
        // 测试结果
        System.out.println("----------------------------------------------------------------------------------------");
        System.out.println("总请求数：" + threadNumber * threadPerRequestCount);
        System.out.println("总耗时：" + stopwatch.getDuration().toMillis() + "ms");
        System.out.println("成功次数：" + successCount.get());
        System.out.println("失败次数：" + failCount.get());
        System.out.println("成功率：" + (double) successCount.get() / (successCount.get() + failCount.get()));
        System.out.println("平均耗时：" + (double) stopwatch.getDuration().toMillis() / (successCount.get() + failCount.get()) + "ms");
        System.out.println("平均成功率：" + (double) successCount.get() / (successCount.get() + failCount.get()));
        System.out.println("平均失败率：" + (double) failCount.get() / (successCount.get() + failCount.get()));
    }

    @Test
    public void loadTest_deleteTransaction() {
        int threadNumber = 100;
        int threadPerRequestCount = 10;

        // 准备测试数据
        List<Long> transactionIds = prepareTestData(threadNumber * threadPerRequestCount);

        CountDownLatch countDownLatch = new CountDownLatch(threadNumber);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        StopWatch stopwatch = StopWatch.createStarted();
        for (int i = 0; i < threadNumber; i++) {
            int finalI = i;
            new Thread(() -> {
                try {
                    for (int j = 0; j < threadPerRequestCount; j++) {
                        // 获取一个交易 ID 进行删除
                        Long transactionId = transactionIds.get(finalI * threadPerRequestCount + j);
                        ResponseEntity<R> response = restTemplate.exchange(
                                baseUrl + port + "/api/v1/transaction/" + transactionId,
                                HttpMethod.DELETE,
                                null,
                                R.class
                        );
                        HttpStatusCode status = response.getStatusCode();
                        if (status.is2xxSuccessful()) {
                            successCount.incrementAndGet();
                        } else {
                            failCount.incrementAndGet();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
            }).start();
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        stopwatch.stop();
        // 测试结果
        System.out.println("----------------------------------------------------------------------------------------");
        System.out.println("总请求数：" + threadNumber * threadPerRequestCount);
        System.out.println("总耗时：" + stopwatch.getDuration().toMillis() + "ms");
        System.out.println("成功次数：" + successCount.get());
        System.out.println("失败次数：" + failCount.get());
        System.out.println("成功率：" + (double) successCount.get() / (successCount.get() + failCount.get()));
        System.out.println("平均耗时：" + (double) stopwatch.getDuration().toMillis() / (successCount.get() + failCount.get()) + "ms");
        System.out.println("平均成功率：" + (double) successCount.get() / (successCount.get() + failCount.get()));
        System.out.println("平均失败率：" + (double) failCount.get() / (successCount.get() + failCount.get()));
    }

    private List<Long> prepareTestData(int count) {
        List<Long> transactionIds = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            TransactionCreateDTO transactionCreateDTO = new TransactionCreateDTO()
                    .setTransactionCode("delete_test_" + RandomUtil.randomString(4))
                    .setAmount(BigDecimal.valueOf(1000))
                    .setType(1)
                    .setStatus("1");
            ResponseEntity<R> response = restTemplate.postForEntity(
                    baseUrl + port + "/api/v1/transaction",
                    transactionCreateDTO,
                    R.class
            );
            if (response.getStatusCode().is2xxSuccessful()) {
                Map vo = (Map) response.getBody().getData();
                transactionIds.add(Long.valueOf(vo.get("id").toString()));
            }
        }
        return transactionIds;
    }

    @Test
    public void loadTest_modifyTransaction() {
        int threadNumber = 100;
        int threadPerRequestCount = 10;

        TransactionCreateDTO transactionCreateDTO = new TransactionCreateDTO()
                .setTransactionCode("update_test_" + "111")
                .setAmount(BigDecimal.valueOf(1000))
                .setType(1)
                .setStatus("1");
        ResponseEntity<R> createResponse = restTemplate.postForEntity(
                baseUrl + port + "/api/v1/transaction",
                transactionCreateDTO,
                R.class
        );
        if (!createResponse.getStatusCode().is2xxSuccessful()) {
            return;
        }
        Map vo = (Map) createResponse.getBody().getData();
        Long transactionId = Long.valueOf(vo.get("id").toString());

        CountDownLatch countDownLatch = new CountDownLatch(threadNumber);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        StopWatch stopwatch = StopWatch.createStarted();
        for (int i = 0; i < threadNumber; i++) {
            int finalI = i;
            new Thread(() -> {
                try {
                    for (int j = 0; j < threadPerRequestCount; j++) {
                        // 假设修改的 ID 是 1
                        TransactionUpdateDTO transactionUpdateDTO = new TransactionUpdateDTO()
                                .setId(transactionId)
                                .setTransactionCode("update_test_" + "111")
                                .setAmount(BigDecimal.valueOf(2000).add(BigDecimal.valueOf(finalI * threadPerRequestCount + j)))
                                .setType(2)
                                .setStatus("2");
                        ResponseEntity<R> response = restTemplate.exchange(
                                baseUrl + port + "/api/v1/transaction/" + transactionId,
                                HttpMethod.PUT,
                                new HttpEntity<>(transactionUpdateDTO),
                                R.class
                        );
                        HttpStatusCode status = response.getStatusCode();
                        if (status.is2xxSuccessful()) {
                            successCount.incrementAndGet();
                        } else {
                            failCount.incrementAndGet();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
            }).start();
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        stopwatch.stop();
        // 测试结果
        System.out.println("----------------------------------------------------------------------------------------");
        System.out.println("总请求数：" + threadNumber * threadPerRequestCount);
        System.out.println("总耗时：" + stopwatch.getDuration().toMillis() + "ms");
        System.out.println("成功次数：" + successCount.get());
        System.out.println("失败次数：" + failCount.get());
        System.out.println("成功率：" + (double) successCount.get() / (successCount.get() + failCount.get()));
        System.out.println("平均耗时：" + (double) stopwatch.getDuration().toMillis() / (successCount.get() + failCount.get()) + "ms");
        System.out.println("平均成功率：" + (double) successCount.get() / (successCount.get() + failCount.get()));
        System.out.println("平均失败率：" + (double) failCount.get() / (successCount.get() + failCount.get()));
    }

    @Test
    public void loadTest_listAllTransactions() {
        int threadNumber = 100;
        int threadPerRequestCount = 10;

        CountDownLatch countDownLatch = new CountDownLatch(threadNumber);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        StopWatch stopwatch = StopWatch.createStarted();
        for (int i = 0; i < threadNumber; i++) {
            new Thread(() -> {
                try {
                    for (int j = 0; j < threadPerRequestCount; j++) {
                        ResponseEntity<R> response = restTemplate.getForEntity(
                                baseUrl + port + "/api/v1/transaction/list",
                                R.class
                        );
                        HttpStatusCode status = response.getStatusCode();
                        if (status.is2xxSuccessful()) {
                            successCount.incrementAndGet();
                        } else {
                            failCount.incrementAndGet();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
            }).start();
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        stopwatch.stop();
        // 测试结果
        System.out.println("----------------------------------------------------------------------------------------");
        System.out.println("总请求数：" + threadNumber * threadPerRequestCount);
        System.out.println("总耗时：" + stopwatch.getDuration().toMillis() + "ms");
        System.out.println("成功次数：" + successCount.get());
        System.out.println("失败次数：" + failCount.get());
        System.out.println("成功率：" + (double) successCount.get() / (successCount.get() + failCount.get()));
        System.out.println("平均耗时：" + (double) stopwatch.getDuration().toMillis() / (successCount.get() + failCount.get()) + "ms");
        System.out.println("平均成功率：" + (double) successCount.get() / (successCount.get() + failCount.get()));
        System.out.println("平均失败率：" + (double) failCount.get() / (successCount.get() + failCount.get()));
    }




}
