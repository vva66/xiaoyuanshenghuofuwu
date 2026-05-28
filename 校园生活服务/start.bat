@echo off
chcp 65001 >nul
echo ========================================
echo     校园服务系统 - 快速启动脚本
echo ========================================
echo.

echo [1/3] 检查MySQL数据库...
echo 请确保MySQL已启动并执行了database.sql脚本
pause

echo.
echo [2/3] 启动后端服务...
start "后端服务" cmd /k "cd backend && mvn spring-boot:run"
echo 后端服务正在启动，请稍候...
timeout /t 10 /nobreak >nul

echo.
echo [3/3] 启动前端服务...
start "前端服务" cmd /k "cd frontend && npm run dev"
echo 前端服务正在启动，请稍候...

echo.
echo ========================================
echo     启动完成！
echo ========================================
echo.
echo 后端地址: http://localhost:8080/api
echo 前端地址: http://localhost:3000
echo.
echo 按任意键退出...
pause >nul
