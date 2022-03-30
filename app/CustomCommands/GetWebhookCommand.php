<?php

namespace RDM\App\CustomCommands;
use RDM\App\Libraries\Database;
use \Telegram\Bot\Commands\Command;
class GetWebhookCommand extends Command
{
    /**
     * @var string Command Name
     */
    protected $name="getwebhook";
    /**
     * @var string Command Description
     */
    protected $description="GetWebhook Command, Get current webhook url";


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
            $msg="Webhook wasn't set.Enter /setwebhook [url] to add new webhook";
        }else{
            $msg="Current webhook: "+$row["webhook"];
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