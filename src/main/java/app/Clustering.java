package app;

import IO.JsonReader;
import cluster.Cluster;
import cluster.DocumentVector;
import cluster.Linkage;
import cluster.SimiliarityFunctions;
import indexer.SimpleIndex;
import org.json.simple.parser.ParseException;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Clustering {
    public static void main(String[] args) throws IOException, ParseException {
        JsonReader jsonReader1 = new JsonReader("IndexInfo.json");
        SimpleIndex index = new SimpleIndex(jsonReader1.read());
        index.load();

        SimiliarityFunctions sf = new SimiliarityFunctions(index);
        double start = 0.05;
        for(Double theshold = start;theshold<1.0;theshold+=0.05) {
            List<Cluster> clusters = new ArrayList<>();
            PrintWriter pw = new PrintWriter("cluster-"+theshold+".out");
            for (int docId = 1; docId <= index.getDocNum(); docId++) {
                DocumentVector docVector = index.getDocVector(docId);
                double max = -1;
                int maxindex = -1;
                for (int i = 0; i < clusters.size(); i++) {
                    double score = clusters.get(i).score(docVector);
                    if (max < score) {
                        max = score;
                        maxindex = i;
                    }

                }
                if (max > -1 && max > theshold) {
                    clusters.get(maxindex).add(docVector);
                } else {
                    Cluster c = new Cluster(clusters.size() + 1, Linkage.MEAN, sf);
                    c.add(docVector);
                    clusters.add(c);
                }
            }
            for (Cluster c : clusters) {
                for(int id:c.getDocIds())
                pw.println(c.getId() + " " + index.docToScene(id));
            }
            pw.close();
        }
    }
}
