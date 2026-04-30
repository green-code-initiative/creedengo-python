if [ $# -eq 0 ]; then
    echo "Error missing parameter"
    exit 1
fi
TEST_CLASS=$1
mvn clean test -Dtest=$TEST_CLASS -DtrimStack