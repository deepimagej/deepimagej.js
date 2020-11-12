package deepimagej;
import ij.ImagePlus;

public interface Promise {
    void resolveImagePlus(ImagePlus image);
    void resolveString(String output);
    void reject(String error);
}
