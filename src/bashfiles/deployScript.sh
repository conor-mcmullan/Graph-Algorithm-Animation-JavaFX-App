
# SET ENVIRONMENT VARIABLES 
echo "SETTING ENV VARIABLES"

# Java 13 JDK Path 
export JAVA_13="E:\Users\McMully\Desktop\jdk-13\bin\java"

# JavaFX 13 Open JDK detacted lib path
export JAVA_FX_13="E:\Users\McMully\Desktop\javafx-sdk-13.0.1\lib"

# Path to the JavaFX Project Jar you wish to execute
export PATH_TO_JAR="E:\Users\McMully\Desktop\csc3002-computer-science-project-pk01\out\artifacts\csc3002_computer_science_project_pk01_jar\csc3002-computer-science-project-pk01.jar"

# DO NOT ALTER 
# This var is used to alter what paths the system searches in
export JAR_EXECUTION="True"

# Launching the system 
echo "[LAUNCHING JAVA JAR]"
${JAVA_13} --module-path ${JAVA_FX_13} --add-modules javafx.controls,javafx.fxml,javafx.graphics --add-reads javafx.graphics=ALL-UNNAMED --add-opens javafx.graphics/com.sun.javafx.perf=ALL-UNNAMED -jar ${PATH_TO_JAR}

