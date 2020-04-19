DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
	`id` int(11) unsigned NOT NULL AUTO_INCREMENT,
	`name` varchar(64) NOT NULL DEFAULT '',
	`password` varchar(128) NOT NULL DEFAULT '',
	`salt` varchar(32) NOT NULL DEFAULT '',
	`head_url` varchar(256) NOT NULL DEFAULT '',
	PRIMARY KEY (`id`),
	UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `news`;
CREATE TABLE `news` (
	`id` int(11) unsigned NOT NULL AUTO_INCREMENT,
	`title` varchar(128) NOT NULL DEFAULT '',
	`link` varchar(256) NOT NULL DEFAULT '',
	`image` varchar(256) NOT NULL DEFAULT '',
	`like_count` int NOT NULL,
	`comment_count` int NOT NULL,
	`created_date` datetime NOT NULL,
	`user_id` int(11) NOT NULL,
	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS login_ticket;
CREATE TABLE login_ticket(
  id int not null auto_increment,
  user_id int not null,
  ticket varchar(45) not null,
  expired datetime not null,
  status int null default 0,
  primary key(id),
  unique index ticket_unique (ticket asc)
)ENGINE=InnoDB default charset=utf8;

DROP TABLE IF EXISTS comment;
CREATE TABLE comment(
  id int not null auto_increment,
  content text not null,
  user_id int not null,
  entity_id int not null,
  entity_type int not null,
  created_date datetime not null,
  status int not null default 0,
  primary key(id),
  index entity_index (entity_id asc ,entity_type asc)
)ENGINE=InnoDB default charset=utf8;


DROP TABLE IF EXISTS message;
CREATE TABLE message(
  id int not null auto_increment,
  from_id int not null,
  to_id int not null,
  content text not null,
  created_date datetime not null,
  has_read int not null,
  conversation_id varchar(20),
  primary key(id),
  index conversation_index (conversation_id)
)ENGINE=InnoDB default charset=utf8;