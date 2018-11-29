#!/bin/bash

SCRIPT=$(readlink -f $0) # Absolute path to this script.
SCRIPTPATH=`dirname $SCRIPT` # Absolute path this script is in. /home/user/bin
#DIR="/home/jacamino/Documents/EIT-DataScience/AMI3/Presentation/sPCA/spca-spark"
SPARK_HOME="/opt/spark-2.4.0-bin-hadoop2.7"
MASTER="local[4]"
RAW="/home/jacamino/Downloads/ml-20m/ratingss.csv.out"
INPUT="/home/jacamino/Documents/EIT-DataScience/AMI3/Presentation/sPCA/spca-spark/input/demoFiles"
OUTPUT="/home/jacamino/Documents/EIT-DataScience/AMI3/Presentation/sPCA/spca-spark/output/demoFiles"
OUTPUT_FORMAT="DENSE"
ERR_SAMPLING="0.01"

#COMPUTE
ROWS=$(awk  -F ',' 'BEGIN{max=0}{if(($1)>max)  max=($1)}END {print max}' ${RAW})
COLUMNS=$(awk  -F ',' 'BEGIN{max=0}{if(($2)>max)  max=($2)}END {print max}' ${RAW})

#PRE-COMPUTe
ROWS=138493
COLUMNS=131263

#VAR
K="10"
N_ITER="3"

if [ "$1" == "create" ]
then
    java -classpath ${SCRIPTPATH}/target/sparkPCA-1.0.jar -DInput=${RAW} -DInputFmt=COO -DOutput=${INPUT} -DBase=0 -DCardinality=${COLUMNS} org.qcri.sparkpca.FileFormat
    exit
fi


echo "#Rows: ${ROWS}"
echo "#Columns: ${COLUMNS}"

SPARK_DEFAULT=""
if [ "$1" == "spark" ]
then
    SPARK_DEFAULT="-Ddefault=true"
fi

$SPARK_HOME/bin/spark-submit --class org.qcri.sparkpca.SparkPCA --master $MASTER --driver-java-options "${SPARK_DEFAULT} -Di=${INPUT} -Do=${OUTPUT} -Drows=${ROWS} -Dcols=${COLUMNS} -Dpcs=${K} -DerrSampleRate=${ERR_SAMPLING} -DmaxIter=${N_ITER} -DoutFmt=${OUTPUT_FORMAT} -DComputeProjectedMatrix=1" ${SCRIPTPATH}/target/sparkPCA-1.0.jar
