package cn.edu.ruc.cloudcomputing.book.chapter05;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class Chapter05E1WordCountAndSort {
    // 继承Mapper接口，设置map的输入类型为<Object,Text>
    // 输出类型为<Text，IntWritable>
    public static class Chapter05E1WordCountAndSortMapper extends Mapper<Object, Text, Text, IntWritable> {
        // one表示单词出现一次
        private final static IntWritable one = new IntWritable(1);
        // word用于存储切下的单词
        private Text word = new Text();

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            StringTokenizer itr = new StringTokenizer(value.toString()); // 对输入的行切词
            while (itr.hasMoreTokens()) {
                word.set(itr.nextToken()); // 切下的单词存入word
                context.write(word, one);
            }
        }
    }

    // 继承Reducer接口，设置Reduce的输入类型为<Text,IntWritable>
    // 输出类型为<Text，IntWritable>

    public static class Chapter05E1WordCountAndSortReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        // result记录单词的频数
        private IntWritable result = new IntWritable();

        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException,
                InterruptedException {
            int sum = 0;
            // 对获取的<key,value-list>计算value的和
            for (IntWritable val : values) {
                sum += val.get();
            }
            // 将频数设置到result中
            result.set(sum);
            // 收集结果
            context.write(key, result);
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        // 检查运行命令
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if (otherArgs.length != 2) {
            System.err.println("Usage: Chapter05E1WordCountAndSort <in> <out>");
            System.exit(2);
        }
        // 配置作业名
        Job job = Job.getInstance(conf, "Chapter05E1WordCountAndSort");
        // 配置作业的各个类
        job.setJarByClass(Chapter05E1WordCountAndSort.class);
        job.setMapperClass(Chapter05E1WordCountAndSortMapper.class);
        job.setCombinerClass(Chapter05E1WordCountAndSortReducer.class);
        job.setReducerClass(Chapter05E1WordCountAndSortReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
        FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}