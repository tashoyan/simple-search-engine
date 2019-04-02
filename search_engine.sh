#!/bin/sh

set -o nounset
set -o errexit

work_dir="$(cd "$(dirname -- "$0")" ; pwd)"
jar_file="$(ls $work_dir/target/*-assembly-*.jar | grep -vi javadoc || true)"
if test -z "$jar_file"
then
    echo "Cannot find the application jar file. Is the project built? Exiting."
    exit 1
fi

java -jar "$jar_file" songs_db
