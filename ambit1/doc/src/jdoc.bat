javac -classpath C:\j2sdk1.4.2_06\lib\tools.jar ListFilesDoclet.java


rem javadoc -classpath C:\j2sdk1.4.2_05\lib\tools.jar  -doclet ambit.doc.ListFilesDoclet -docletpath d:\src\ambit\src\ambit\doc -sourcepath d:\src\ambit\src -package ambit.data.domain ambit.data
javadoc -classpath C:\j2sdk1.4.2_06\lib\tools.jar  -doclet ListFilesDoclet -docletpath d:\src\ambit\src\ambit\doc -sourcepath d:\src\ambit\src -package ambit -subpackages data domain


