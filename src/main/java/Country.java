import org.ejml.simple.SimpleMatrix;
public class Country {
    double population;
    double populationDensity;
    double populationMax = 1390000000;
    double populationDensityMax = 18713;
    static int[] months = {31,29,31,30,31,30,31,31,30,31,30,31};
    static int[] maxs = {209, 4290259, 148011, 1846641, 2816444, 77255, 3887, 140050};
    SimpleMatrix[] inputs;
    SimpleMatrix[] outputs;
    boolean updated = false;
    public Country(String[][] csv){
        inputs = new SimpleMatrix[csv.length];
        outputs = new SimpleMatrix[inputs.length];
        //System.out.println(outputs.length);
        int index = 0;
        for(String[] row : csv){
            String[] date = row[0].split("\\/");
            SimpleMatrix input = new SimpleMatrix(8,1);
            SimpleMatrix output = new SimpleMatrix(9,1);
            input.set(0,0, daysSince(Integer.parseInt(date[0])-1, Integer.parseInt(date[1])));
            output.set(0,0, daysSince(Integer.parseInt(date[0])-1, Integer.parseInt(date[1])));
            for(int i = 2; i < 9; i++){
                input.set(i-1,0, Integer.parseInt(row[i]));
                output.set(i-1, 0, 0);
            }
            output.set(8, 0, 0);
            outputs[index] = output;
            inputs[index] = input;
            index++;
        }

    }

    public static int daysSince(int month, int day){
        int total = 0;
        for(int i = 0; i < month; i++){
                total+=months[i];
        }
        total+=day;
        return total;
    }
    public SimpleMatrix getInput(int index){
        SimpleMatrix A = new SimpleMatrix(9,1);
        for(int i = 1; i < 8; i++){
            A.set(i-1, 0, 2*(inputs[index].get(i,0)/maxs[i])-1);
        }
        A.set(7,0,2*(this.population/this.populationMax)-1);
        A.set(8,0,2*(this.populationDensity/this.populationDensityMax)-1);
        return A;
    }
    public SimpleMatrix getError(SimpleMatrix output, int index){
        double Current = .5;
        double New = .5;
        double Intesity = 0;
        SimpleMatrix errors = new SimpleMatrix(8,1);
        for(int i = 0 ; i < 8; i++){
            double error =
                    Current*(2*(this.inputs[index+10].get(1,0)/4290259)-1) +
                    New*(2*(this.inputs[index+10].get(5,0)/77255)-1)+
                    Intesity*(2*(output.get(i,0)/4)-1);

            errors.set(i,0, error);
        }
        return errors;
    }
}
