package myhia;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class InvertCitationJob extends Configured implements Tool {

    public static class MapClass extends MapReduceBase implements Mapper<Text, Text, Text, Text> {

        @Override
        public void map(final Text key, final Text value, final OutputCollector<Text, Text> output, final Reporter reporter) throws IOException {

            output.collect(value, key);
        }
    }

    public static class ReduceClass extends MapReduceBase implements Reducer<Text, Text, Text, Text> {

        @Override
        public void reduce(final Text key, final Iterator<Text> values, final OutputCollector<Text, Text> output, final Reporter reporter) throws IOException {

            String csv = "";
            while (values.hasNext()) {
                if (csv.length() > 0) {
                    csv += ",";
                }
                csv += values.next();
            }
            output.collect(key, new Text(csv));
        }
    }


    @Override
    public int run(final String[] strings) throws Exception {

        Configuration configuration = getConf();
        JobConf jobConf = new JobConf(configuration, InvertCitationJob.class);

        Path in = new Path(strings[0]);
        Path out = new Path(strings[1]);
        FileInputFormat.setInputPaths(jobConf, in);
        FileOutputFormat.setOutputPath(jobConf, out);

        jobConf.setInputFormat(KeyValueTextInputFormat.class);
        jobConf.setOutputFormat(TextOutputFormat.class);

        jobConf.setMapperClass(MapClass.class);
        jobConf.setReducerClass(ReduceClass.class);

        jobConf.setOutputKeyClass(Text.class);
        jobConf.setOutputValueClass(Text.class);
        jobConf.set("key.value.separator.in.input.line", ",");

        jobConf.setJobName("Inverted Citation Job");

        JobClient.runJob(jobConf);

        return 0;
    }

    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new Configuration(), new InvertCitationJob(), args);

        System.exit(res);
    }
}
