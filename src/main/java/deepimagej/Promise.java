package deepimagej;

import ij.ImagePlus;

public interface Promise {
    void resolve(ImagePlus output);
    void reject(String error);
}
