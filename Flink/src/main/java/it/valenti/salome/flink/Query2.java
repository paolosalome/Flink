package main.java.it.valenti.salome.flink;

import main.java.it.valenti.salome.flink.util.TupleTreeSet;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.java.tuple.*;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.timestamps.AscendingTimestampExtractor;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.streaming.connectors.rabbitmq.RMQSource;
import org.apache.flink.streaming.connectors.rabbitmq.common.RMQConnectionConfig;
import org.apache.flink.streaming.util.serialization.SimpleStringSchema;
import org.apache.flink.util.Collector;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Set;
import java.util.TreeSet;


/**
 * Created by root on 27/06/17.
 */
public class Query2 {
    private final static String QUEUE_NAME = "CODA";
    private final static int DEFAULT_SIZE = 1;
    public static final class LineSplitter implements FlatMapFunction<String, Tuple6<String, Integer, Long, Double, Double, Double>> {

        /** normalizza e divide le linee lette dalla sorgente */

        private static final long serialVersionUID = -6087546114124934588L;

        @Override
        public void flatMap(String value, Collector<Tuple6<String, Integer, Long, Double, Double, Double>> out) {
            String[] tokens = value.toLowerCase().split(",");
            // id,counteggio,timestamp,*,*,v
            out.collect(new Tuple6<>(tokens[0], 1, Long.parseLong(tokens[1]), 0d,0d,
                     Double.parseDouble(tokens[5])));

        }
    }

    public static final class Output implements FlatMapFunction<Tuple6<String, Integer, Long, Double, Double, Double>, Tuple4<Long, Long, String, Double>> {

        private static final long serialVersionUID = -6087546114124934588L;

        /** converte id -> nome */
        @Override
        public void flatMap(Tuple6<String, Integer, Long, Double, Double, Double> input, Collector<Tuple4<Long, Long, String, Double>> output) throws Exception {

            switch (input.f0) {
                case "13":
                    input.f0 = "Nick Gertje";
                    break;
                case "14":
                    input.f0 = "Nick Gertje";
                    break;
                case "47":
                    input.f0 = "Dennis Dotterweich";
                    break;
                case "16":
                    input.f0 = "Dennis Dotterweich";
                    break;
                case "49":
                    input.f0 = "Niklas Waelzlein";
                    break;
                case "88":
                    input.f0 = "Niklas Waelzlein";
                    break;
                case "19":
                    input.f0 = "Wili Sommer";
                    break;
                case "52":
                    input.f0 = "Wili Sommer";
                    break;
                case "53":
                    input.f0 = "Philipp Harlass";
                    break;
                case "54":
                    input.f0 = "Philipp Harlass";
                    break;
                case "23":
                    input.f0 = "Roman Hartleb";
                    break;
                case "24":
                    input.f0 = "Roman Hartleb";
                    break;
                case "57":
                    input.f0 = "Erik Engelhardt";
                    break;
                case "58":
                    input.f0 = "Erik Engelhardt";
                    break;
                case "59":
                    input.f0 = "Sandro Schneider";
                    break;
                case "28":
                    input.f0 = "Sandro Schneider";
                    break;
                //Inizio Team2
                case "61":
                    input.f0 = "Leon Krapf";
                    break;
                case "62":
                    input.f0 = "Leon Krapf";
                    break;
                case "63":
                    input.f0 = "Kevin Baer";
                    break;
                case "64":
                    input.f0 = "Kevin Baer";
                    break;
                case "65":
                    input.f0 = "Luca Ziegler";
                    break;
                case "66":
                    input.f0 = "Luca Ziegler";
                    break;
                case "67":
                    input.f0 = "Ben Mueller";
                    break;
                case "68":
                    input.f0 = "Ben Mueller";
                    break;
                case "69":
                    input.f0 = "Vale Reitstetter";
                    break;
                case "38":
                    input.f0 = "Vale Reitstetter";
                    break;
                case "71":
                    input.f0 = "Christopher Lee";
                    break;
                case "40":
                    input.f0 = "Christopher Lee";
                    break;
                case "73":
                    input.f0 = "Leon Heinze";
                    break;
                case "74":
                    input.f0 = "Leon Heinze";
                    break;
                case "75":
                    input.f0 = "Leo Langhans";
                    break;
                case "44":
                    input.f0 = "Leo Langhans";
                    break;


            }
        //start-fine-name-velocità

            output.collect(new Tuple4<>(input.f2,input.f3.longValue(), input.f0, input.f5));
        }
    }

    public static final class FinalOutput implements FlatMapFunction<Tuple4<Long, Long, String, Double>,String> {

        private static final long serialVersionUID = -6087546114124934588L;

        @Override
        public void flatMap(Tuple4<Long, Long, String, Double> input, Collector<String> output) throws Exception {
            output.collect(input.f2);
        }
    }

    public static void main(String[] args) throws Exception {

        /** configurazione */

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        int timeWindow= DEFAULT_SIZE;
        if(args.length != 4)
            System.out.println("Usage: .\\bin\\flink run -c main.java.it.valenti.salome.flink.Query3 .\\flink.jar <fileOut,sizeWindow in minutes, parallelism,source(0= flink/1=file)>");

        DataStream<String> stream;

        if(args[3].equals("0")) {
            final RMQConnectionConfig connectionConfig = new RMQConnectionConfig.Builder()
                    .setHost("localhost")
                    .setPort(5672)
                    .setVirtualHost("/")
                    .setUserName("guest")
                    .setPassword("guest")
                    .setConnectionTimeout(5000)
                    // .setTopologyRecoveryEnabled(false)
                    .build();
            System.out.println("Prima di dataStrem");
            stream = env
                    .addSource(new RMQSource<String>(
                            connectionConfig,            // config for the RabbitMQ connection
                            QUEUE_NAME,                 // name of the RabbitMQ queue to consume
                            true,   // use correlation ids; can be false if only at-least-once is required
                            new SimpleStringSchema()));   // deserialization schema to turn messages into Java objects
        }
        else
            stream = env.readTextFile("..\\FilterFile.txt");

        if(args[1]!=null)
            timeWindow = Integer.parseInt(args[1]);
        if(args[2]!=null)
            env.setParallelism(Integer.parseInt(args[2]));
        final long EndWindow = timeWindow*60000;

        /** si calcola la velocità media di ogni giocatore all'interno della finestra */
        DataStream<Tuple6<String, Integer, Long, Double, Double, Double>> ex =
                stream.flatMap(new LineSplitter())
                        .assignTimestampsAndWatermarks(new AscendingTimestampExtractor<Tuple6<String, Integer, Long, Double, Double, Double>>() {

                            @Override
                            public long extractAscendingTimestamp(Tuple6<String, Integer, Long, Double, Double, Double> element) {
                                return element.f2;
                            }
                        }).keyBy(0)
                        .window(TumblingEventTimeWindows.of(Time.minutes(timeWindow)))
                        .reduce(new ReduceFunction<Tuple6<String, Integer, Long, Double, Double, Double>>() {


                            private static final long serialVersionUID = 7448326084914869599L;

                            @Override
                            public Tuple6<String, Integer, Long, Double, Double, Double> reduce(Tuple6<String, Integer, Long, Double, Double, Double> value1, Tuple6<String, Integer, Long, Double, Double, Double> value2)
                                    throws Exception {
                                double media = 0;
                                double time = value1.f2;
                                if (value2 != null) {
                                    media = value1.f5 + (value2.f5 - value1.f5) / (value1.f1 + 1);
                                    time = value2.f2;
                                }
                                // id-counteggio,start,end,y,velocità media
                                return new Tuple6<>(value1.f0, value1.f1 + 1, value1.f2, time , value1.f4, media);
                            }
                        });


        /** si effettua la media tra i sensori associati a ciascun giocatore */
        DataStream<Tuple4<Long, Long, String, Double>> query1 = ex.flatMap(new Output())
                .keyBy(2)
                .countWindow(2)
                .reduce(new ReduceFunction<Tuple4<Long, Long, String, Double>>() {
                    @Override
                    public Tuple4<Long, Long, String, Double> reduce(Tuple4<Long, Long, String, Double> value1, Tuple4<Long, Long, String, Double> value2) throws Exception {
                        double avg_speed ;
                        if (value2 != null) {
                            avg_speed = (value1.f3 + value2.f3) / 2;
                        } else {
                            avg_speed = value1.f3;
                        }
                        return new Tuple4<>(value1.f0, value2.f1, value2.f2, avg_speed);
                    }
                });
        /** si attendono i valori finali riguardanti la media della velocità per ogni giocatore.
         * quindi si attendono 16 tuple per farne l'ordinamento mediante l'utilizzo dei treeSet
         **/
        DataStream<String> query2= query1.countWindowAll(16)
                .reduce(new ReduceFunction<Tuple4<Long, Long, String, Double>>() {
                    Set<TupleTreeSet> treeSet = new TreeSet<>();
                    double count=0;

                    @Override
                    public Tuple4<Long, Long, String, Double> reduce(Tuple4<Long, Long, String, Double> value1, Tuple4<Long, Long, String, Double> value2) throws Exception {

                        int c=5;
                        String set="";
                        if(count==15){
                            count =0;
                            treeSet = new TreeSet<>();
                        }
                        if(count == 0)
                            treeSet.add(new TupleTreeSet(value1.f2,value1.f3));
                        treeSet.add(new TupleTreeSet(value2.f2,value2.f3));
                        NumberFormat format = new DecimalFormat("###.##");

                        for (TupleTreeSet t : treeSet) {
                            set+="  id:"+t.getId()+", velocità:"+format.format(t.getVel());
                            c--;
                            if(c<=0) break;
                            else set+=" ; ";
                        }
                        count+=1.0;
                        Long endWindow = (value1.f0/EndWindow+1)*EndWindow;

                        String out = "t_start:"+value1.f0 +", t_end:"+ endWindow+",<"+set+" >";

                        return new Tuple4<>(value1.f0,value2.f1,out,count);
                    }
                }).flatMap(new FinalOutput());

        query2.writeAsText(args[0], FileSystem.WriteMode.NO_OVERWRITE);
        // execute program
        env.execute("Flink Streaming Java API Skeleton");
    }

}
