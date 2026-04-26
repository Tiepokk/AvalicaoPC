@echo off
title Sistema de Producao de Veiculos

cd src

echo Compilando arquivos...
javac veiculo/*.java fabrica/*.java loja/*.java cliente/*.java
if %errorlevel% neq 0 (
    echo Erro na compilacao. Verifique o codigo.
    pause
    exit /b
)

start "FABRICA" cmd /k "java fabrica.Fabrica"

timeout /t 2 /nobreak > nul

start "LOJA 1" cmd /k "java loja.LojaMain 1"
start "LOJA 2" cmd /k "java loja.LojaMain 2"
start "LOJA 3" cmd /k "java loja.LojaMain 3"

timeout /t 2 /nobreak > nul

start "CLIENTES" cmd /k "java cliente.Cliente"

echo ==========================================
echo Sistema distribuido iniciado com sucesso!
echo ==========================================

cd ..
pause