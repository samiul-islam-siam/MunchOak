@REM 安装到本地

CALL ../mvnw clean install -f ../pom.xml -DskipTests %*
pause
