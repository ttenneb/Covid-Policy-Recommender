import org.ejml.simple.SimpleMatrix;

public class Output {
    double max;
    public SimpleMatrix output;
    public String prediction;
    public SimpleMatrix[] hidden;
    public Output(SimpleMatrix output, String[] key, SimpleMatrix[] hidden){
        this.output=output;
        this.hidden =hidden;
        //find maximum of outputs
        max = 0;
        int index = 0;
        for(int i = 0; i<output.numRows(); i++){
            if (output.get(i,0) > max) {
                max = output.get(i, 0);
                index = i;
            }
        }
        prediction=key[index];
    }
    public SimpleMatrix getOutput(){
        SimpleMatrix A = new SimpleMatrix(8,1);
        for(int i = 0; i < 8; i++){
            A.set(i,0,(int)(4*output.get(i,0)));
        }
        return A;
    }
}
