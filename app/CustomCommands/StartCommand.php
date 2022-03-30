<?php

namespace RDM\App\CustomCommands;
use Telegram\Bot\Commands\Command;

class StartCommand extends Command
{
    /**
     * @var string Command Name
     */
    protected $name="start";
    /**
     * @var string Command Description
     */
    protected $description="Start Command, Start interacting with the our bot";

    /**
     * @inheritDoc
     */
    public function handle($arguments)
    {
    $text="Hello ".$arguments["firstname"]."🖐
    \nThis bot will help you to download any file remotely
    \nUse below commands to get started
    \n /setwebhook [url] to set your webhook(insert your webhook url without the brackets)
    \n /getwebhook to see your current webhook
    \n /help to see all available commands
           ";
        $this->replyWithMessage([
            "text"=>$text,
            "parse_mode"=>"html"
        ]);
    }
}