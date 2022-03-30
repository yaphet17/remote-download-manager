<?php

namespace RDM\App\Libraries;
require_once __DIR__."/../../vendor/autoload.php";
use Monolog\Handler\StreamHandler;
use Monolog\Logger;
use Monolog\Processor\WebProcessor;
class LogWriter
{
    private $logger;

    public function __construct(string $channel)
    {
        $this->logger=new Logger($channel);
        $this->logger->pushProcessor(new WebProcessor());
    }

    public function setInfoLogger(string $message,array $context){
        $logPath=__DIR__."/logs/info_log.log";
        $logStream=new StreamHandler($logPath,Logger::INFO);
        $this->logger->pushHandler($logStream);
        $this->logger->info($message,$context);
    }
    public function setErrorLogger(string $message,array $context){
        $logPath=__DIR__."/logs/error_log.log";
        $logStream=new StreamHandler($logPath,Logger::ERROR);
        $this->logger->pushHandler($logStream);
        $this->logger->error($message,$context);
    }
    public function setDebugLogger(string $message,array $context){
        $logPath=__DIR__."/logs/debug_log.log";
        $logStream=new StreamHandler($logPath,Logger::DEBUG);
        $this->logger->pushHandler($logStream);
        $this->logger->debug($message,$context);
    }
    public function setAlertLogger(string $message,array $context){
        $logPath=__DIR__."/logs/alert_log.log";
        $logStream=new StreamHandler($logPath,Logger::ALERT);
        $this->logger->pushHandler($logStream);
        $this->logger->alert($message,$context);
    }
    public function setEmergencyLogger(string $message,array $context){
        $logPath=__DIR__."/logs/emergency.log";
        $logStream=new StreamHandler($logPath,Logger::EMERGENCY);
        $this->logger->pushHandler($logStream);
        $this->logger->emergency($message,$context);
    }

}