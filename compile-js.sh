# This script compiles imagej into image.js
# It requires CHEERPJ_DIR and IJ_DIR
set -e

# compile from scratch
if [ -z ${CHEERPJ_DIR+x} ]
then
    if [[ "$OSTYPE" == "linux-gnu"* ]]; then
        curl https://d3415aa6bfa4.leaningtech.com/cheerpj_linux_2.1.tar.gz -LO
        tar -xvf cheerpj_linux_2.1.tar.gz
        export CHEERPJ_DIR=$(pwd)/cheerpj_2.1
    else
        echo "Please download cheerpj from https://www.leaningtech.com/pages/cheerpj.html#Download and set the CHEERPJ_DIR env variable "
        exit 1
    fi
fi

mvn clean
mvn install:install-file -Dfile=${CHEERPJ_DIR}/cheerpj-dom.jar -DgroupId=com.learningtech -DartifactId=cheerpj-dom -Dversion=1.0 -Dpackaging=jar
mvn package

sh get-imagej.sh

IJ_DIR=$(pwd)/dist/ij153

cp target/DeepImageJ_JS_-2.0.1-SNAPSHOT.jar ${IJ_DIR}/plugins

cd ${IJ_DIR}
# remove plugins/DeepImageJ_JS_-2.0.1-SNAPSHOT.jar if exists
rm -f plugins/DeepImageJ_JS_-2.0.1-SNAPSHOT.jar

# java -jar ij-1.53h.jar
${CHEERPJ_DIR}/cheerpjfy.py --deps=ij-1.53h.jar -j 4 plugins/DeepImageJ_JS_-2.0.1-SNAPSHOT.jar