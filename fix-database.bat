@echo off
echo ========================================
echo 数据库修复脚本 - 添加 user_id 字段
echo ========================================
echo.

set DB_HOST=sh-cynosdbmysql-grp-jfavuxao.sql.tencentcdb.com
set DB_PORT=24565
set DB_USER=zhengzj
set DB_PASS=Aa62770212
set DB_NAME=aquaculture

echo 正在连接数据库...
echo 主机: %DB_HOST%:%DB_PORT%
echo 数据库: %DB_NAME%
echo.

mysql -h %DB_HOST% -P %DB_PORT% -u %DB_USER% -p%DB_PASS% %DB_NAME% < "%~dp0aquaculture-backend\src\main\resources\db\migration_add_user_id.sql"

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo 修复成功！
    echo ========================================
    echo.
    echo 请重启后端服务以使更改生效。
) else (
    echo.
    echo ========================================
    echo 修复失败！错误代码: %ERRORLEVEL%
    echo ========================================
    echo.
    echo 请检查:
    echo 1. MySQL 客户端是否已安装
    echo 2. 数据库连接信息是否正确
    echo 3. 网络连接是否正常
)

pause
