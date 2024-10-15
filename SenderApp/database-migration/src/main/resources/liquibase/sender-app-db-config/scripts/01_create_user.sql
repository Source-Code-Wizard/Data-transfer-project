alter session set "_ORACLE_SCRIPT"=true;
create user sender_app_db identified by "sender_app_db";
grant sysdba to sender_app_db;
grant all privileges to sender_app_db;

exit;
