package administration.server.domain;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Average {

    private double result;

    public Average() {}

    public Average(double result) {
        this.result = result;
    }

    public double getResult() {
        return result;
    }

    public void setResult(double result) {
        this.result = result;
    }
}