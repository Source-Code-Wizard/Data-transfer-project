alter session set "_ORACLE_SCRIPT"=true;
create user receiver_app_db identified by "receiver_app_db";
grant sysdba to receiver_app_db;
grant all privileges to receiver_app_db;

exit;
