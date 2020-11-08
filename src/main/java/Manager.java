import org.ejml.simple.SimpleMatrix;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;


public class Manager extends Game {
    static Engine engine ;

    static Hashtable<String, Country> countries = new Hashtable<>();

    static SimpleNeuralNetwork nn = new SimpleNeuralNetwork(9, 1, 32, 8);
    public static void main(String args[]) throws IOException {
        engine = new Engine(new Manager());

        //organize and format all input data into vectors for network
        InputStream A = Manager.class.getResourceAsStream("/full_grouped.csv");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(A))) {
            String line;
            String country = "Afghanistan";
            br.readLine();
            ArrayList<String[]> rows = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                String[] row = line.split(",");
                if(country.equals(row[1])){
                    rows.add(row);
                }else if(!country.equals("")){
                    String[][] data = new String[rows.size()][9];
                    for(int i = 0; i < data.length; i ++){
                        for(int j = 0 ; j< 9; j++){
                            data[i][j]=rows.get(i)[j];
                        }
                    }
                    countries.put(country, new Country(data));
                    country = row[1];
                    rows.clear();
                    rows.add(row);
                }
            }
            String[][] data = new String[rows.size()][9];
            for(int i = 0; i < data.length; i ++){
                for(int j = 0 ; j< 9; j++){
                    data[i][j]=rows.get(i)[j];
                }
            }
            countries.put(country, new Country(data));

        }
        // organize all output data into vectors
        addPolicy("/covid-19-testing-policy.csv", 1);
        addPolicy("/face-covering-policies-covid.csv", 2);
        addPolicy("/income-support-covid.csv", 3);
        addPolicy("/international-travel-covid.csv", 4);
        addPolicy("/public-campaigns-covid.csv", 5);
        addPolicy("/public-gathering-rules-covid.csv", 6);
        addPolicy("/stay-at-home-covid.csv", 7);
        addPolicy("/workplace-closures-covid.csv", 8);

        A = Manager.class.getResourceAsStream("/CountryInfo.csv");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(A))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] row = line.split(",");
                System.out.println(row[0]);
                countries.get(row[0]).populationDensity = Double.parseDouble(row[1]);
                countries.get(row[0]).population= Integer.parseInt(row[2]);
            }

        }

        for(SimpleMatrix a : countries.get("Zimbabwe").outputs){
            a.print();
        }
        System.out.println(countries.get("US").population);

        final int TRAINING_LENGTH = 100000;
        int count = 0;
        for(int i = 0; i < TRAINING_LENGTH; i++) {
            countries.forEach((k, v) -> {
                if(v.updated)
                    nn.train(v, (int) Math.random() * 177);
            });
            System.out.println(100*(double)count/TRAINING_LENGTH + "%");
            count++;
        }
        double[][] in = {{.9},{.1},{.1},{.4},{.9},{.4},{.2},{1},{1}};


        SimpleMatrix m = new SimpleMatrix(in);
        nn.feedforward(m).getOutput().print();
        double[][] in2 = {{-.9},{-.8},{.2},{-.8},{-.9},{-.7},{.2},{.5},{-.2}};


         m = new SimpleMatrix(in2);
        nn.feedforward(m).getOutput().print();
        double[][] in3 = {{.9},{.1},{.1},{.4},{.9},{.4},{.2},{-1},{-1}};

        m = new SimpleMatrix(in2);
        nn.feedforward(m).getOutput().print();
        double[][] in4 = {{.3},{.1},{.1},{.1},{0},{0},{.2},{1},{.5}};


        m = new SimpleMatrix(in4);
        nn.feedforward(m).getOutput().print();
    }

    @Override
    public void update(Input i) {

    }

    @Override
    public void renderer(Renderer r) {
        r.clear();
    }
    public static void addPolicy(String csv, int i){
        int count = 0;
        InputStream A = Manager.class.getResourceAsStream(csv);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(A))) {
            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] row = line.split(",");
                String[] date = row[2].split("\\/");
                int days = Country.daysSince(Integer.parseInt(date[0]), Integer.parseInt(date[1]));
                if( days >= 22 && days <= 209 && countries.containsKey(row[0])){
                    countries.get(row[0]).updated=true;
                    countries.get(row[0]).outputs[days-22].set(i,0, Integer.parseInt(row[3]));
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
