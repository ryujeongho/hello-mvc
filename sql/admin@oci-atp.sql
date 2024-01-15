-- 계정생성
create user web
identified by "비밀번호"
default tablespace data;

grant connect, resource to web;

alter user web quota unlimited on data;