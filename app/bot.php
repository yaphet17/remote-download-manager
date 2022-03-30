<?php
require_once __DIR__."/../vendor/autoload.php";
use Telegram\Bot\Commands\HelpCommand;
use Telegram\Bot\Exceptions\TelegramSDKException;
use \RDM\App\CustomCommands\StartCommand;
use \RDM\App\CustomCommands\SetWebhookCommand;
use \RDM\App\CustomCommands\GetWebhookCommand;
use \RDM\App\CustomCommands\DeleteWebhookCommand;

//load bot token from .env file
$dotenv = Dotenv\Dotenv::createImmutable(__DIR__."/..");
$dotenv->load();

$token= $_ENV['RDM_TOKEN'];

try {
    $telegram = new \Telegram\Bot\Api($token);
} catch (TelegramSDKException $e) {
    die($e->getMessage());
}

//add available commands
$commands=[
    StartCommand::class,
    SetWebhookCommand::class,
    GetWebhookCommand::class,
    DeleteWebhookCommand::class,
    HelpCommand::class
];
//enable commands
$commandHandler=$telegram->commandsHandler(true);
//add command to command bus
$telegram->addCommands($commands);
$update=$telegram->getWebhookUpdates();
$updateId=!empty($update->getUpdateId())?$update->getUpdateId():null;

//if there is no update:exit
if($updateId==null){
    print_r(array("message"=>"Not update found"));
    die();
}
$message=$update->getMessage();
$text=$message->getText();
$chat=$message->getChat();
$chatId=$chat->getId();
$firstName=$chat->getFirstName();
$username=$chat->getUsername();
//if the message is command:handle command
if(str_starts_with($text,"/")){
    //remove  the first '/' character
    $text=substr($text,1);
    //if the command contains parameter parse
    if(str_contains(" ",$text)){
        $text=explode(" ",$text);
        $command=FILTER_SANITIZE_STRING($text[0]);
        $url=FILTER_SANITIZE_URL($text[1]);
        $paramArray=array("firstname"=>$firstName,"username"=>$username,"chat_id"=>$chatId,"url"=>$url);
    }else{
        $command=$text;
        $paramArray=array("firstname"=>$firstName,"username"=>$username);
    }
    $response=$telegram->getCommandBus()->execute($command,$paramArray,$commandHandler);
}else{
    $telegram->sendMessage([
                "chat_id"=>$chatId,
                "text"=>"Invalid command use /help to see all available commands",
                "parse_mode"=>"html"
    ]);
}