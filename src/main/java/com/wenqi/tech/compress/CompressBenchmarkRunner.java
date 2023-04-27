package com.wenqi.tech.compress;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.RunnerException;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author liangwenqi
 * @date 2023/3/23
 */
public class CompressBenchmarkRunner {

    /**
     * # Run complete. Total time: 00:04:02
     *
     * Benchmark                               Mode  Cnt  Score   Error  Units
     * CompressBenchmarkRunner.testCompress    avgt  100  4.382 ± 0.369  ms/op
     * CompressBenchmarkRunner.testDeCompress  avgt  100  0.849 ± 0.026  ms/op
     *
     */
    public static void main(String[] args) throws IOException, RunnerException {
        org.openjdk.jmh.Main.main(args);
    }


    //@Benchmark
    //@BenchmarkMode(Mode.AverageTime)
    //@OutputTimeUnit(TimeUnit.MILLISECONDS)
    //@Fork(value = 1)
    //@Warmup(iterations = 2)
    //@Measurement(iterations = 100)
    //@Threads(20)
    //public void testCompress() {
    //    GzipUtils.compress(StringMaterial.bigValue);
    //}
    //
    //@Benchmark
    //@BenchmarkMode(Mode.AverageTime)
    //@OutputTimeUnit(TimeUnit.MILLISECONDS)
    //@Fork(value = 1)
    //@Warmup(iterations = 2)
    //@Measurement(iterations = 100)
    //@Threads(20)
    //public void testCompress2() {
    //    GzipUtils2.compress(StringMaterial.bigValue);
    //}
    //
    //@Benchmark
    //@BenchmarkMode(Mode.AverageTime)
    //@OutputTimeUnit(TimeUnit.MILLISECONDS)
    //@Fork(value = 1)
    //@Warmup(iterations = 2)
    //@Measurement(iterations = 100)
    //@Threads(20)
    //public void testDeCompress() {
    //    GzipUtils.deCompress(StringMaterial.compressValue);
    //}
    //
    //@Benchmark
    //@BenchmarkMode(Mode.AverageTime)
    //@OutputTimeUnit(TimeUnit.MILLISECONDS)
    //@Fork(value = 1)
    //@Warmup(iterations = 2)
    //@Measurement(iterations = 100)
    //@Threads(20)
    //public void testDeCompress2() throws IOException {
    //    GzipUtils2.extract(StringMaterial.compressValue);
    //}
    //
    //@Benchmark
    //@BenchmarkMode(Mode.AverageTime)
    //@OutputTimeUnit(TimeUnit.MILLISECONDS)
    //@Fork(value = 1)
    //@Warmup(iterations = 2)
    //@Measurement(iterations = 100)
    //@Threads(20)
    //public void testTotal() {
    //    String origin = StringMaterial.bigValue;
    //    String compress = GzipUtils.compress(origin);
    //    String handle = GzipUtils.deCompress(compress);
    //    if (!Objects.equals(origin, handle)) {
    //        System.out.println("not equal!");
    //    }
    //}

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Fork(value = 1)
    @Warmup(iterations = 2)
    @Measurement(iterations = 100)
    @Threads(20)
    public void testTotal2() throws IOException {
        String origin = StringMaterial.bigValue;
        String compress = GzipUtils2.compress(origin);
        String handle = GzipUtils2.extract(compress);
        if (!Objects.equals(origin, handle)) {
            System.out.println("not equal!");
        }
    }
}
