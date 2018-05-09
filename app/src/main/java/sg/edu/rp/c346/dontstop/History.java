package sg.edu.rp.c346.dontstop;

public class History {
    private String duration;
    private double distance;
    private String date;

    public History(String duration, double distance) {
        this.duration = duration;
        this.distance = distance;
    }

    public History() {
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
