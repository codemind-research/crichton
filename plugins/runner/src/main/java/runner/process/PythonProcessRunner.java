package runner.process;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Slf4j
public abstract class PythonProcessRunner extends ProcessRunner {

    private static final String PYTHON_VERSION_2 = "python";
    private static final String PYTHON_VERSION_3 = "python3";

    
    @Override
    protected String getProcessName() {
        // TODO: 추후 properties를 통해 Python 경로로 해서 반환 처리 해야함

        String currentPython = PYTHON_VERSION_2;

        boolean isPython2Installed = checkPython(PYTHON_VERSION_2);
        if(isPython2Installed) {
            log.debug("Python 2.x installed");
            currentPython = PYTHON_VERSION_2;
        }


        boolean isPython3Installed = checkPython(PYTHON_VERSION_3);
        if(isPython3Installed) {
            log.debug("Python 3.x installed");
            currentPython = PYTHON_VERSION_3;
        }

        if(isPython2Installed && isPython3Installed) {
            log.info("Using Python 3.x");
        }
        else if(!isPython2Installed && !isPython3Installed){
            throw new RuntimeException("Python is not installed");
        }

        return currentPython;

    }


    private static boolean checkPython(String command) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command, "--version");
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            log.debug("Checking {}:", command);

            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                builder.append(line).append(System.lineSeparator());
            }

            log.debug(builder.toString());

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                return true;
            } else {
                return false;
            }


        } catch (Exception e) {
            return false;
        }
    }
}
