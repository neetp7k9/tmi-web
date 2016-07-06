GLOBAL_INDEX_DIRECTORY=./searchServer/index/global
LOCAL_INDEX_DIRECTORY=./searchServer/index/local
TEXT_INDEX_DIRECTORY=./searchServer/index/text
TMP_INDEX_DIRECTORY=./searchServer/data
TMP_DIRECTORY=./public/tmp

if [ ! -d "$GLOBAL_INDEX_DIRECTORY" ]; then
  mkdir $GLOBAL_INDEX_DIRECTORY
fi
if [ ! -d "$LOCAL_INDEX_DIRECTORY" ]; then
  mkdir $LOCAL_INDEX_DIRECTORY
fi
if [ ! -d "$TEXT_INDEX_DIRECTORY" ]; then
  mkdir $TEXT_INDEX_DIRECTORY
fi
if [ ! -d "$TMP_INDEX_DIRECTORY" ]; then
  mkdir $TMP_INDEX_DIRECTORY
fi
if [ ! -d "$TMP_DIRECTORY" ]; then
  mkdir $TMP_DIRECTORY
fi
cd searchServer
java -Djava.library.path=/usr/local/share/OpenCV/java/:./lib/native/native/osx/ -classpath "./out/production/main:./target/sparkexample.jar:./target/sparkexample-jar-with-dependencies.jar:./lib/lire.jar:./lib/jopensurf.jar:./lib/opencv-2412.jar:./lib/jopensurf-src.jar:./lib/commons-io-2.4.jar:./lib/commons-math3-3.2.jar:./lib/lucene-core-5.2.1.jar:./lib/lucene-core-5.5.0.jar:./lib/commons-codec-1.10.jar:./lib/lucene-codecs-5.5.0.jar:./lib/pdfbox-app-2.0.0-RC3.jar:./lib/lucene-queryparser-5.2.1.jar:./lib/lucene-queryparser-5.5.0.jar:./lib/lucene-analyzers-common-5.2.1.jar:./lib/lucene-analyzers-common-5.5.0.jar" EagleEye.Server
