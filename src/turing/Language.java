package turing;

import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Sam Wright
 * Date: 24/11/2012
 * Time: 19:48
 */
public interface Language {
    Set<Character> get();
    void checkUniversal();
}
