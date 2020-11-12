package deepimagej;

public interface Promise {
    void resolve(Object output);
    void reject(String error);
}
