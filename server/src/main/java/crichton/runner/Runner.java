package crichton.runner;

import java.util.Optional;

public interface Runner {
    Optional<RunResult> run() throws Exception;
    CommandBuilder builder() ;
}
