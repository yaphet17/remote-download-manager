<?php

namespace RDM\App\CustomCommands;
use RDM\App\Libraries\Database;
use \Telegram\Bot\Commands\Command;
class DeleteWebhookCommand extends Command
{
    /**
     * @var string Command Name
     */
    protected $name="deletewebhook";
    /**
     * @var string Command Description
     */
    protected $description="DeleteWebhook Command, Delete current webhook url";



    /**
     * @inheritDoc
     */
    public function handle($arguments)
    {
        $username=$arguments["username"];
        $db=new Database();
        $query="SELECT * FROM user WHERE user_id=:username";
        $db->query($query);
        $db->bind(":username",$username);
        $row=$db->single();
        if($db->rowCount()>0&&empty($row["webhook"])){
            //error message
            $msg="Webhook is already deleted.Enter /setwebhook [url] to add new webhook";
        }else{
            $query="DELETE FROM user WHERE user_id=:username";
            $db->query($query);
            $db->bind(":username",$username);
            $db->execute();
            //successful message
            $msg="Webhook successfully deleted";
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