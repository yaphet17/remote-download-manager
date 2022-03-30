<?php

namespace RDM\App\CustomCommands;
use RDM\App\Libraries\Database;
use \Telegram\Bot\Commands\Command;

class SetWebhookCommand extends Command
{
    /**
     * @var string Command Name
     */
    protected $name="setwebhook";
    /**
     * @var string Command Description
     */
    protected $description="SetWebhook Command, Set webhook url to connect to your computer
    \n/setwebhook [url]";

    /**
     * @inheritDoc
     */
    public function handle($arguments)
    {
        $username=$arguments["username"];
        $chatId=$arguments["chat_id"];
        $url=$arguments["webhook"];
        $db=new Database();
        $query="SELECT webhook FROM user WHERE user_id=:username";
        $db->query($query);
        $db->bind(":username",$username);
        $row=$db->single();
        //check if webhook already exist for a user
        if($db->rowCount()>0&&!empty($row)){
            //error message
            $msg="Webhook is already set.Enter /deletewebhook to delete existing webhook";
        }else{
            $query="INSERT INTO user VALUES(:username,:chat_id,:url)";
            $db->query($query);
            $db->bind(":username",$username);
            $db->bind(":chat_id",$chatId);
            $db->bind(":url",$url);
            $db->execute();
            //successful message
            $msg="Webhook successfully set";
        }
        $this->replyMsg($msg);
    }
    private function replyMsg($msg){
        $this->replyWithMessage([
            "text"=>$msg,
            "parse_mode"=>"html"
        ]);
    }
}