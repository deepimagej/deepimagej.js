set -e
mvn clean
mvn install:install-file -Dfile=${CHEERPJ_DIR}/cheerpj-dom.jar -DgroupId=com.learningtech -DartifactId=cheerpj-dom -Dversion=1.0 -Dpackaging=jar
mvn package


cp target/DeepImageJ_JS_-2.0.1-SNAPSHOT.jar ${IJ_DIR}/plugins

cd ${IJ_DIR}
# java -jar ij-1.53h.jar
${CHEERPJ_DIR}/cheerpjfy.py --deps=ij-1.53h.jar -j 4 plugins/DeepImageJ_JS_-2.0.1-SNAPSHOT.jar