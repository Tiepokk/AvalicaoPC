@echo off
title Sistema de Producao de Veiculos

if not exist "out" mkdir "out"

echo Compilando arquivos para a pasta out...
javac -d out -sourcepath src src/fabrica/*.java src/loja/*.java src/cliente/*.java src/veiculo/*.java

if %errorlevel% neq 0 (
    echo Erro na compilacao. Verifique o codigo.
    pause
    exit /b
)

echo Iniciando componentes...

start "FABRICA" cmd /k "java -cp out fabrica.Fabrica"

timeout /t 2 /nobreak > nul

start "LOJA 1" cmd /k "java -cp out loja.LojaMain 1"
start "LOJA 2" cmd /k "java -cp out loja.LojaMain 2"
start "LOJA 3" cmd /k "java -cp out loja.LojaMain 3"

timeout /t 2 /nobreak > nul

start "CLIENTES" cmd /k "java -cp out cliente.Cliente"

echo ==========================================
echo Sistema distribuido iniciado com sucesso!
echo ==========================================

pause
