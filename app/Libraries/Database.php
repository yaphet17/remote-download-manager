<?php

namespace RDM\App\Libraries;

require_once __DIR__ . "/../../vendor/autoload.php";
require_once __DIR__ . "/../Config/config.php";

use PDO;

class Database{
    private $dbHost=DB_HOST;
    private $dbName=DB_NAME;
    private $dbUser=DB_USER;
    private $dbPass=DB_PASS;

    private $statement;
    private $dbHandler;
    private $logger;
    private $error;


    public function __construct(){
        $conn='mysql:host='.$this->dbHost.';dbname='.$this->dbName;
        $options=array(
            PDO::ATTR_PERSISTENT=>true,
            PDO::ATTR_ERRMODE=>PDO::ERRMODE_EXCEPTION
        );
        //instantiate logger
        $this->logger=new LogWriter("database");
        try{
            $this->dbHandler= new PDO($conn,$this->dbUser,$this->dbPass,$options);

        }catch(PDOException $e){
            $this->error=$e->getMessage();
            $this->logger->setEmergencyLogger("can't connect to database.",array("exception"=>$e));
            echo $this->error;
        }

    }

    public function query($sql){
        try{
            $this->statement=$this->dbHandler->prepare($sql);
        }catch(\PDOException $e){
            $this->logger->setErrorLogger("can't prepare query",
                array("exception"=>$e));
            die("can't prepare query");
        }


    }
    public function  bind($parameter,$value,$type=null){
        switch(is_null($type)){
            case is_int($value):
                $type=PDO::PARAM_INT;
                break;
            case is_bool($value):
                $type=PDO::PARAM_BOOL;
                break;
            case is_null($value):
                $type=PDO::PARAM_NULL;
                break;
            default:
                $type=PDO::PARAM_STR;

        }
        $result=$this->statement->bindValue($parameter,$value,$type);
        try{
            if(!$result){
                throw new QueryErrorException("failed to bind value: param=
                     {$parameter} value={$value} type={$type}");
            }
        }catch(QueryErrorException $e){
            $this->logger->setErrorLogger("can't bind value",array("context"=>$e));
            die("can't bind value");
        }
    }

    public function execute(){
        try{
            $result=$this->statement->execute();
            if(!$result){
                throw new QueryErrorException("failed to execute query");
            }
            echo "<p>Succesfully added</p>";
        }catch(\QueryErrorException $e){
            $this->logger->setErrorLogger("can't execute query",array("exception"=>$e));
            die("can't execute query");
        }
        catch(\PDOException $e){
            $this->logger->setErrorLogger("can't execute query",array("exception"=>$e));
            die("can't execute query");
        }

        return ;
    }

    public function resultSet(){
        $this->execute();
        return $this->statement->fetchAll(PDO::FETCH_ASSOC);
    }
    public function single(){
        $this->execute();
        return $this->statement->fetch(PDO::FETCH_ASSOC);
    }
    public function rowCount(){
        return $this->statement->rowCount();
    }


}
$db=new Database();
