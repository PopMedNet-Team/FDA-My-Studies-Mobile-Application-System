-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               5.7.25-log - MySQL Community Server (GPL)
-- Server OS:                    Win64
-- HeidiSQL Version:             9.5.0.5196
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- Dumping database structure for fda_hphc
DROP DATABASE IF EXISTS `fda_hphc`;
CREATE DATABASE IF NOT EXISTS `fda_hphc` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `fda_hphc`;

-- Dumping structure for table fda_hphc.activetask_formula
DROP TABLE IF EXISTS `activetask_formula`;
CREATE TABLE IF NOT EXISTS `activetask_formula` (
  `activetask_formula_id` int(11) NOT NULL AUTO_INCREMENT,
  `value` varchar(255) DEFAULT NULL,
  `formula` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`activetask_formula_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.active_task
DROP TABLE IF EXISTS `active_task`;
CREATE TABLE IF NOT EXISTS `active_task` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `study_id` int(11) DEFAULT NULL,
  `frequency` varchar(255) DEFAULT NULL,
  `task_name` varchar(100) DEFAULT NULL,
  `duration` varchar(100) DEFAULT NULL,
  `repeat_questionnaire` int(11) DEFAULT NULL,
  `active_task_lifetime_start` date DEFAULT NULL,
  `active_task_lifetime_end` date DEFAULT NULL,
  `day_of_the_week` varchar(255) DEFAULT NULL,
  `repeat_active_task` int(11) DEFAULT NULL,
  `created_by` int(11) DEFAULT NULL,
  `created_on` varchar(255) DEFAULT NULL,
  `modified_by` int(11) DEFAULT NULL,
  `modified_on` varchar(255) DEFAULT NULL,
  `display_name` varchar(255) DEFAULT NULL,
  `instruction` varchar(255) DEFAULT NULL,
  `short_title` varchar(255) DEFAULT NULL,
  `created_date` varchar(255) DEFAULT NULL,
  `modified_date` varchar(255) DEFAULT NULL,
  `task_title` varchar(255) DEFAULT NULL,
  `task_type` int(11) DEFAULT NULL,
  `task_type_id` int(11) DEFAULT NULL,
  `action` tinyint(4) DEFAULT NULL,
  `study_version` int(11) DEFAULT NULL,
  `version` float DEFAULT NULL,
  `custom_study_id` varchar(255) DEFAULT NULL,
  `is_live` int(11) DEFAULT NULL,
  `is_Change` int(11) DEFAULT NULL,
  `active` tinyint(1) DEFAULT '1',
  `anchor_date_id` int(11) DEFAULT NULL,
  `schedule_type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `study_id_idx` (`study_id`),
  CONSTRAINT `FK_study_active_task_id` FOREIGN KEY (`study_id`) REFERENCES `studies` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=2850 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.active_task_attrtibutes_values
DROP TABLE IF EXISTS `active_task_attrtibutes_values`;
CREATE TABLE IF NOT EXISTS `active_task_attrtibutes_values` (
  `active_task_attribute_id` int(11) NOT NULL AUTO_INCREMENT,
  `active_task_id` int(11) NOT NULL DEFAULT '0',
  `active_task_master_attr_id` int(11) NOT NULL,
  `attribute_val` varchar(100) DEFAULT NULL,
  `add_to_line_chart` char(50) DEFAULT NULL,
  `time_range_chart` varchar(100) DEFAULT NULL,
  `rollback_chat` varchar(100) DEFAULT NULL,
  `title_chat` varchar(100) DEFAULT NULL,
  `use_for_statistic` char(1) DEFAULT NULL,
  `identifier_name_stat` varchar(100) DEFAULT NULL,
  `display_name_stat` varchar(100) DEFAULT NULL,
  `display_units_stat` varchar(100) DEFAULT NULL,
  `upload_type_stat` varchar(100) DEFAULT NULL,
  `formula_applied_stat` varchar(100) DEFAULT NULL,
  `time_range_stat` varchar(100) DEFAULT NULL,
  `study_version` int(11) DEFAULT NULL,
  `active` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`active_task_attribute_id`),
  KEY `FK_active_task_attrtibutes_values_active_task_master_attribute` (`active_task_master_attr_id`),
  KEY `FK_active_task_attrtibutes_values_active_task` (`active_task_id`),
  CONSTRAINT `FK_active_task_attrtibutes_values_active_task` FOREIGN KEY (`active_task_id`) REFERENCES `active_task` (`id`),
  CONSTRAINT `FK_active_task_attrtibutes_values_active_task_master_attribute` FOREIGN KEY (`active_task_master_attr_id`) REFERENCES `active_task_master_attribute` (`active_task_master_attr_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7576 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.active_task_custom_frequencies
DROP TABLE IF EXISTS `active_task_custom_frequencies`;
CREATE TABLE IF NOT EXISTS `active_task_custom_frequencies` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `frequency_start_date` date DEFAULT NULL,
  `frequency_end_date` date DEFAULT NULL,
  `frequency_time` time DEFAULT NULL,
  `active_task_id` int(11) DEFAULT NULL,
  `study_version` int(11) DEFAULT NULL,
  `is_used` char(1) DEFAULT NULL,
  `time_period_from_days` int(11) DEFAULT NULL,
  `time_period_to_days` int(11) DEFAULT NULL,
  `x_days_sign` bit(1) DEFAULT NULL,
  `y_days_sign` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `active_task_id_FK` (`active_task_id`),
  CONSTRAINT `active_task_id_FK` FOREIGN KEY (`active_task_id`) REFERENCES `active_task` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=85 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.active_task_frequencies
DROP TABLE IF EXISTS `active_task_frequencies`;
CREATE TABLE IF NOT EXISTS `active_task_frequencies` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `active_task_id` int(11) DEFAULT NULL,
  `frequency_date` date DEFAULT NULL,
  `frequency_time` time DEFAULT NULL,
  `is_launch_study` tinyint(1) DEFAULT NULL,
  `is_study_life_time` tinyint(1) DEFAULT NULL,
  `study_version` int(11) DEFAULT NULL,
  `time_period_from_days` int(11) DEFAULT NULL,
  `time_period_to_days` int(11) DEFAULT NULL,
  `x_days_sign` bit(1) DEFAULT NULL,
  `y_days_sign` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `active_task_id_idx` (`active_task_id`),
  KEY `FKBBE7F3598EB972DD` (`active_task_id`),
  CONSTRAINT `FKBBE7F3598EB972DD` FOREIGN KEY (`active_task_id`) REFERENCES `active_task` (`id`),
  CONSTRAINT `FK_active_task_fre_id` FOREIGN KEY (`active_task_id`) REFERENCES `active_task` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=7239 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.active_task_list
DROP TABLE IF EXISTS `active_task_list`;
CREATE TABLE IF NOT EXISTS `active_task_list` (
  `active_task_list_id` int(11) NOT NULL AUTO_INCREMENT,
  `task_name` varchar(100) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`active_task_list_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.active_task_master_attribute
DROP TABLE IF EXISTS `active_task_master_attribute`;
CREATE TABLE IF NOT EXISTS `active_task_master_attribute` (
  `active_task_master_attr_id` int(11) NOT NULL AUTO_INCREMENT,
  `task_type_id` int(11) NOT NULL,
  `order_by` int(11) DEFAULT NULL,
  `attribute_type` varchar(100) DEFAULT NULL,
  `attribute_name` varchar(100) DEFAULT NULL,
  `display_name` varchar(250) DEFAULT NULL,
  `attribute_data_type` varchar(100) DEFAULT NULL,
  `add_to_dashboard` char(1) DEFAULT NULL,
  `task_type` int(11) DEFAULT NULL,
  `study_version` int(11) DEFAULT NULL,
  PRIMARY KEY (`active_task_master_attr_id`),
  KEY `FK_active_task_master_attribute_active_task_list` (`task_type_id`),
  CONSTRAINT `FK_active_task_master_attribute_active_task_list` FOREIGN KEY (`task_type_id`) REFERENCES `active_task_list` (`active_task_list_id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.active_task_select_options
DROP TABLE IF EXISTS `active_task_select_options`;
CREATE TABLE IF NOT EXISTS `active_task_select_options` (
  `active_task_select_options_id` int(11) NOT NULL AUTO_INCREMENT,
  `active_task_master_attr_id` int(11) NOT NULL,
  `option_val` varchar(100) NOT NULL,
  PRIMARY KEY (`active_task_select_options_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.active_task_steps
DROP TABLE IF EXISTS `active_task_steps`;
CREATE TABLE IF NOT EXISTS `active_task_steps` (
  `step_id` int(11) NOT NULL,
  `active_task_id` int(11) DEFAULT NULL,
  `active_task_stepscol` varchar(45) DEFAULT NULL,
  `sd_live_form_id` varchar(45) DEFAULT NULL COMMENT 'start complete / live / question form / instruction',
  `sequence_no` int(11) DEFAULT NULL,
  `study_version` int(11) DEFAULT NULL,
  PRIMARY KEY (`step_id`),
  KEY `active_task_id_idx` (`active_task_id`),
  KEY `FKAFC1CAC68EB972DD` (`active_task_id`),
  CONSTRAINT `FKAFC1CAC68EB972DD` FOREIGN KEY (`active_task_id`) REFERENCES `active_task` (`id`),
  CONSTRAINT `FK_active_task_steps_id` FOREIGN KEY (`active_task_id`) REFERENCES `active_task` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.anchordate_type
DROP TABLE IF EXISTS `anchordate_type`;
CREATE TABLE IF NOT EXISTS `anchordate_type` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `custom_study_id` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `has_anchortype_draft` int(11) DEFAULT NULL,
  `study_id` int(11) DEFAULT NULL,
  `version` float DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=113 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.app_versions
DROP TABLE IF EXISTS `app_versions`;
CREATE TABLE IF NOT EXISTS `app_versions` (
  `av_id` int(11) NOT NULL AUTO_INCREMENT,
  `app_version` float DEFAULT NULL,
  `created_on` timestamp NULL DEFAULT NULL,
  `force_update` int(11) DEFAULT NULL,
  `os_type` varchar(255) DEFAULT NULL,
  `bundle_id` varchar(255) DEFAULT NULL,
  `custom_study_id` varchar(255) DEFAULT NULL,
  `message` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`av_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.audit_log
DROP TABLE IF EXISTS `audit_log`;
CREATE TABLE IF NOT EXISTS `audit_log` (
  `audit_log_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `activity` varchar(200) NOT NULL,
  `activity_details` varchar(2000) NOT NULL,
  `class_method_name` varchar(100) NOT NULL,
  `created_date_time` varchar(50) NOT NULL,
  PRIMARY KEY (`audit_log_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3246 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.auth_info
DROP TABLE IF EXISTS `auth_info`;
CREATE TABLE IF NOT EXISTS `auth_info` (
  `auth_id` int(11) NOT NULL,
  `participant_id` int(11) DEFAULT NULL,
  `device_token` varchar(1000) DEFAULT NULL,
  `device_type` char(1) DEFAULT NULL,
  `created_on` datetime DEFAULT NULL,
  `modified_on` datetime DEFAULT NULL,
  `auth_key` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`auth_id`),
  KEY `participant_id_idx` (`participant_id`),
  CONSTRAINT `FK_participant_auth_info_id` FOREIGN KEY (`participant_id`) REFERENCES `participant_details` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.bar_chart
DROP TABLE IF EXISTS `bar_chart`;
CREATE TABLE IF NOT EXISTS `bar_chart` (
  `id` int(11) NOT NULL,
  `data_source` int(11) DEFAULT NULL COMMENT 'question id / active task id',
  `range_type` tinyint(1) DEFAULT NULL COMMENT 'Time based / Other',
  `custom` tinyint(1) DEFAULT NULL COMMENT 'Y / N',
  `custom_start` datetime DEFAULT NULL,
  `custom_end` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.bar_chart_axis
DROP TABLE IF EXISTS `bar_chart_axis`;
CREATE TABLE IF NOT EXISTS `bar_chart_axis` (
  `id` int(11) NOT NULL,
  `bar_chart_id` int(11) DEFAULT NULL,
  `range_start` varchar(50) DEFAULT NULL,
  `range_end` varchar(50) DEFAULT NULL,
  `display_name` varchar(50) DEFAULT NULL,
  `bar_color` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `bar_chart_id_idx` (`bar_chart_id`),
  CONSTRAINT `FK_bar_chart_id` FOREIGN KEY (`bar_chart_id`) REFERENCES `bar_chart` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.branding
DROP TABLE IF EXISTS `branding`;
CREATE TABLE IF NOT EXISTS `branding` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `study_id` int(11) DEFAULT NULL,
  `background` varchar(20) DEFAULT NULL,
  `font` varchar(20) DEFAULT NULL,
  `tint` varchar(20) DEFAULT NULL,
  `logo_image_path` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `study_id_idx` (`study_id`),
  CONSTRAINT `FK_study_branding_id` FOREIGN KEY (`study_id`) REFERENCES `studies` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.charts
DROP TABLE IF EXISTS `charts`;
CREATE TABLE IF NOT EXISTS `charts` (
  `id` int(11) NOT NULL,
  `study_id` int(11) DEFAULT NULL,
  `reference_id` int(11) DEFAULT NULL COMMENT 'Pie chart id / Bar chart id .. etc',
  `chart_title` varchar(200) DEFAULT NULL,
  `sequence_no` int(11) DEFAULT NULL,
  `chart_type` varchar(100) DEFAULT NULL COMMENT 'Pie Chart / Bar Chart / Line Chart ..etc',
  `time_range` varchar(50) DEFAULT NULL COMMENT 'current day / current week / current month / custom range',
  `allow_previous_next` tinyint(1) DEFAULT NULL COMMENT 'Y / N',
  PRIMARY KEY (`id`),
  KEY `study_id_idx` (`study_id`),
  CONSTRAINT `FK_study_charts_id` FOREIGN KEY (`study_id`) REFERENCES `studies` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.comprehension_test_question
DROP TABLE IF EXISTS `comprehension_test_question`;
CREATE TABLE IF NOT EXISTS `comprehension_test_question` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `question_text` varchar(500) DEFAULT NULL,
  `study_id` int(11) DEFAULT NULL,
  `sequence_no` int(11) DEFAULT NULL,
  `structure_of_correct_ans` tinyint(1) DEFAULT NULL COMMENT '0 - Any of one marked as correct answers, 1 -  All of the ones marked as correct answers',
  `created_by` int(11) DEFAULT NULL,
  `created_on` datetime DEFAULT NULL,
  `modified_by` int(11) DEFAULT NULL,
  `modified_on` datetime DEFAULT NULL,
  `study_version` int(11) DEFAULT NULL,
  `active` bit(1) DEFAULT NULL,
  `status` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `consent_id_idx` (`study_id`),
  CONSTRAINT `FK_comprehension_test_question_studies` FOREIGN KEY (`study_id`) REFERENCES `studies` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=532 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.comprehension_test_response
DROP TABLE IF EXISTS `comprehension_test_response`;
CREATE TABLE IF NOT EXISTS `comprehension_test_response` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `comprehension_test_question_id` int(11) DEFAULT NULL,
  `response_option` varchar(500) DEFAULT NULL,
  `correct_answer` tinyint(1) DEFAULT NULL COMMENT '1 - Yes, 2 -  No',
  `study_version` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `comprehension_test_question_id_idx` (`comprehension_test_question_id`),
  CONSTRAINT `comprehension_test_question_id` FOREIGN KEY (`comprehension_test_question_id`) REFERENCES `comprehension_test_question` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=1996 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.consent
DROP TABLE IF EXISTS `consent`;
CREATE TABLE IF NOT EXISTS `consent` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `study_id` int(11) DEFAULT NULL,
  `comprehension_test_minimum_score` int(11) DEFAULT NULL,
  `share_data_permissions` varchar(50) DEFAULT NULL,
  `title` varchar(250) DEFAULT NULL,
  `custom_study_id` varchar(255) DEFAULT NULL,
  `study_version` int(11) DEFAULT NULL,
  `is_live` int(11) DEFAULT NULL,
  `tagline_description` varchar(250) DEFAULT NULL,
  `short_description` varchar(250) DEFAULT NULL,
  `long_description` varchar(550) DEFAULT NULL,
  `learn_more_text` longtext,
  `consent_doc_type` varchar(10) DEFAULT NULL,
  `consent_doc_content` longtext,
  `allow_without_permission` varchar(50) DEFAULT NULL,
  `e_consent_firstname` varchar(10) DEFAULT NULL,
  `e_consent_lastname` varchar(10) DEFAULT NULL,
  `e_consent_agree` varchar(10) DEFAULT NULL,
  `e_consent_signature` varchar(10) DEFAULT NULL,
  `e_consent_datetime` varchar(10) DEFAULT NULL,
  `created_on` datetime DEFAULT NULL,
  `created_by` int(11) DEFAULT NULL,
  `modified_on` datetime DEFAULT NULL,
  `modified_by` int(11) DEFAULT NULL,
  `consent_document_type` varchar(50) DEFAULT NULL,
  `html_consent` varchar(255) DEFAULT NULL,
  `affirmation_text` varchar(255) DEFAULT NULL,
  `denial_text` varchar(255) DEFAULT NULL,
  `text_of_the_permission` varchar(255) DEFAULT NULL,
  `version` float DEFAULT NULL,
  `need_comprehension_test` varchar(255) DEFAULT NULL,
  `aggrement_of_consent` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `study_id_idx` (`study_id`),
  CONSTRAINT `FK_study_consent_id` FOREIGN KEY (`study_id`) REFERENCES `studies` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=341 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.consent_info
DROP TABLE IF EXISTS `consent_info`;
CREATE TABLE IF NOT EXISTS `consent_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `study_id` int(11) DEFAULT NULL,
  `consent_item_type` varchar(50) DEFAULT NULL,
  `title` varchar(200) DEFAULT NULL,
  `content_type` varchar(50) DEFAULT NULL,
  `brief_summary` longtext,
  `elaborated` longtext,
  `html_content` longtext,
  `url` varchar(200) DEFAULT NULL,
  `visual_step` tinytext,
  `sequence_no` int(11) DEFAULT '0',
  `created_by` int(11) DEFAULT NULL,
  `created_on` datetime DEFAULT NULL,
  `modified_by` int(11) DEFAULT NULL,
  `modified_on` datetime DEFAULT NULL,
  `display_title` varchar(255) DEFAULT NULL,
  `status` tinyint(4) DEFAULT NULL,
  `consent_item_title_id` int(11) DEFAULT NULL,
  `active` tinyint(4) DEFAULT NULL,
  `study_version` int(11) DEFAULT NULL,
  `custom_study_id` varchar(255) DEFAULT NULL,
  `is_live` int(11) DEFAULT NULL,
  `version` float DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `consent_id_idx` (`study_id`),
  CONSTRAINT `FK_consent_info_studies` FOREIGN KEY (`study_id`) REFERENCES `studies` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=1343 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.consent_master_info
DROP TABLE IF EXISTS `consent_master_info`;
CREATE TABLE IF NOT EXISTS `consent_master_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `code` varchar(250) DEFAULT NULL,
  `study_version` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for procedure fda_hphc.deleteInActiveActivity
DROP PROCEDURE IF EXISTS `deleteInActiveActivity`;
DELIMITER //
CREATE DEFINER=`root`@`localhost` PROCEDURE `deleteInActiveActivity`(
	IN `studyId` INT(11)

)
BEGIN

DELETE
FROM active_task_attrtibutes_values
WHERE active=0 AND active_task_id IN(
SELECT id
FROM active_task
WHERE study_id=studyId);

DELETE
FROM active_task
WHERE active= 0 AND study_id=studyId;

DELETE
FROM questions
WHERE active =0 AND id IN(
SELECT question_id
FROM form_mapping
WHERE active=0 AND form_id IN (
SELECT instruction_form_id
FROM questionnaires_steps
WHERE active=0 AND step_type='Form' AND questionnaires_id IN (
SELECT id
FROM questionnaires q
WHERE active=0 AND study_id=studyId)));

DELETE
FROM response_type_value
WHERE questions_response_type_id IN(
SELECT question_id
FROM form_mapping
WHERE active=0 AND form_id IN (
SELECT instruction_form_id
FROM questionnaires_steps
WHERE active=0 AND step_type='Form' AND questionnaires_id IN (
SELECT id
FROM questionnaires
WHERE active=0 AND study_id=studyId)));

DELETE
FROM response_sub_type_value
WHERE response_type_id IN(
SELECT question_id
FROM form_mapping
WHERE active=0 AND form_id IN (
SELECT instruction_form_id
FROM questionnaires_steps
WHERE active=0 AND step_type='Form' AND questionnaires_id IN (
SELECT id
FROM questionnaires
WHERE active=0 AND study_id=studyId)));

DELETE
FROM questions
WHERE active =0 AND id IN (
SELECT instruction_form_id
FROM questionnaires_steps
WHERE active=0 AND step_type='Question' AND questionnaires_id IN (
SELECT id
FROM questionnaires
WHERE active=0 AND study_id=studyId));

DELETE
FROM response_type_value
WHERE questions_response_type_id IN(
SELECT instruction_form_id
FROM questionnaires_steps
WHERE active=0 AND step_type='Question' AND questionnaires_id IN (
SELECT id
FROM questionnaires
WHERE active=0 AND study_id=studyId));

DELETE
FROM response_sub_type_value
WHERE response_type_id IN(
SELECT instruction_form_id
FROM questionnaires_steps
WHERE active=0 AND step_type='Question' AND questionnaires_id IN (
SELECT id
FROM questionnaires
WHERE active=0 AND study_id=studyId));

DELETE
FROM instructions
WHERE active=0 AND id IN (
SELECT instruction_form_id
FROM questionnaires_steps
WHERE active=0 AND questionnaires_id IN (
SELECT id
FROM questionnaires
WHERE active=0 AND study_id=studyId));

DELETE
FROM questionnaires_steps
WHERE active=0 AND questionnaires_id IN (
SELECT id
FROM questionnaires
WHERE active=0 AND study_id=studyId);

DELETE
FROM questionnaires_frequencies
WHERE questionnaires_id IN (
SELECT id
FROM questionnaires
WHERE active=0 AND study_id=studyId);

DELETE
FROM questionnaires_custom_frequencies
WHERE questionnaires_id IN(
SELECT id
FROM questionnaires
WHERE active=0 AND study_id=studyId);

DELETE
FROM questionnaires
WHERE active=0 AND study_id=studyId;

END//
DELIMITER ;

-- Dumping structure for procedure fda_hphc.deleteQuestionnaire
DROP PROCEDURE IF EXISTS `deleteQuestionnaire`;
DELIMITER //
CREATE DEFINER=`root`@`localhost` PROCEDURE `deleteQuestionnaire`(
	IN `questionnaireId` INT(11),
	IN `modifiedOn` VARCHAR(255),
	IN `modifiedBy` INT(11),
	IN `studyId` INT(50)

)
BEGIN

update questionnaires qbo set qbo.active=0,qbo.modified_by=modifiedBy,qbo.modified_date=modifiedOn where qbo.study_id=studyId and qbo.id=questionnaireId and qbo.active=1;

update instructions ibo,questionnaires_steps qsbo set ibo.active=0,ibo.modified_by=modifiedBy,ibo.modified_on=modifiedOn where ibo.id=qsbo.instruction_form_id and qsbo.questionnaires_id=questionnaireId and qsbo.active=1 and qsbo.step_type='Instruction' and ibo.active=1;

update questions qbo,questionnaires_steps qsbo set qbo.active=0,qbo.modified_by=modifiedBy,qbo.modified_on=modifiedOn where
qbo.id=qsbo.instruction_form_id and qsbo.questionnaires_id=questionnaireId and qsbo.active=1 and qsbo.step_type='Question' and qbo.active=1; 

update questions qbo,form_mapping fmbo,questionnaires_steps qsbo  set qbo.active=0,qbo.modified_by=modifiedBy,qbo.modified_on=modifiedOn,fmbo.active=0 where qbo.id=fmbo.question_id and fmbo.form_id=qsbo.instruction_form_id and qsbo.questionnaires_id=questionnaireId and qsbo.step_type='Form' and qsbo.active=1 and qbo.active=1;

update form fbo,questionnaires_steps qsbo set fbo.active=0,fbo.modified_by=modifiedBy,fbo.modified_on=modifiedOn where fbo.form_id=qsbo.instruction_form_id and qsbo.step_type='Form' and qsbo.questionnaires_id=questionnaireId and qsbo.active=1 and fbo.active=1;

update questionnaires_steps qs set qs.active=0,qs.modified_by=modifiedBy,qs.modified_on=modifiedOn where qs.questionnaires_id=questionnaireId and qs.active=1;

END//
DELIMITER ;

-- Dumping structure for procedure fda_hphc.deleteQuestionnaireFrequencies
DROP PROCEDURE IF EXISTS `deleteQuestionnaireFrequencies`;
DELIMITER //
CREATE DEFINER=`root`@`localhost` PROCEDURE `deleteQuestionnaireFrequencies`(
	IN `questionnaireId` INT(11)

)
BEGIN

delete from questionnaires_custom_frequencies where questionnaires_id=questionnaireId;
delete from questionnaires_frequencies where questionnaires_id=questionnaireId;

END//
DELIMITER ;

-- Dumping structure for procedure fda_hphc.deleteQuestionnaireStep
DROP PROCEDURE IF EXISTS `deleteQuestionnaireStep`;
DELIMITER //
CREATE DEFINER=`root`@`localhost` PROCEDURE `deleteQuestionnaireStep`(
	IN `questionnaireId` INT(11),
	IN `modifiedOn` VARCHAR(255),
	IN `modifiedBy` INT(11),
	IN `sequenceNo` INT(11),
	IN `stepId` INT(11),
	IN `steptype` VARCHAR(255)

)
BEGIN
update questionnaires_steps qs set qs.sequence_no=qs.sequence_no-1,qs.modified_on=modifiedOn,qs.modified_by=modifiedBy where qs.questionnaires_id=questionnaireId and qs.active=1 and qs.sequence_no>=sequenceNo;

if steptype='Instruction' then
update instructions ibo set ibo.active=0,ibo.modified_by=modifiedBy,ibo.modified_on=modifiedOn where ibo.id=stepId and ibo.active=1;

elseif steptype='Question' then
update questions q set q.active=0,q.modified_by=modifiedBy,q.modified_on=modifiedOn where q.id=stepId and q.active=1;
update response_type_value rt set rt.active=0 where rt.questions_response_type_id=stepId and rt.active=1;
Update response_sub_type_value qrsbo set qrsbo.active=0 where qrsbo.response_type_id=stepId and qrsbo.active=1;

elseif steptype='Form' then
update questions QBO,form_mapping FMBO set QBO.active=0,QBO.modified_by=modifiedBy,QBO.modified_on=modifiedOn,FMBO.active=0 where QBO.id=FMBO.question_id and FMBO.form_id=stepId and QBO.active=1 and FMBO.active=1;
Update response_type_value QRBO,form_mapping FMBO set QRBO.active=0 where QRBO.questions_response_type_id=FMBO.question_id and FMBO.form_id=stepId and QRBO.active=1;
Update response_sub_type_value QRSBO,form_mapping FMBO set QRSBO.active=0 where QRSBO.response_type_id=FMBO.question_id and FMBO.form_id=stepId and QRSBO.active=1;
Update form fm set fm.active=0,fm.modified_by=modifiedBy,fm.modified_on=modifiedOn where fm.form_id=stepId and fm.active=1;

END IF;
END//
DELIMITER ;

-- Dumping structure for table fda_hphc.eligibility
DROP TABLE IF EXISTS `eligibility`;
CREATE TABLE IF NOT EXISTS `eligibility` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `study_id` int(11) DEFAULT NULL,
  `eligibility_mechanism` tinyint(2) DEFAULT NULL COMMENT '1 - ID validation only,\n2 - ID validation + Eligibility Test,\n3 - Eligibility Test only',
  `instructional_text` varchar(2500) DEFAULT NULL,
  `failure_outcome_text` varchar(2500) DEFAULT NULL,
  `study_version` int(11) DEFAULT NULL,
  `created_by` int(11) DEFAULT NULL,
  `created_on` varchar(255) DEFAULT NULL,
  `modified_by` int(11) DEFAULT NULL,
  `modified_on` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `study_id_idx` (`study_id`),
  CONSTRAINT `FK_el_study_id` FOREIGN KEY (`study_id`) REFERENCES `studies` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=1058 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.eligibility_test
DROP TABLE IF EXISTS `eligibility_test`;
CREATE TABLE IF NOT EXISTS `eligibility_test` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `eligibility_id` int(11) DEFAULT NULL,
  `short_title` varchar(200) DEFAULT NULL,
  `question` varchar(1000) DEFAULT NULL,
  `response_format` varchar(20) DEFAULT NULL,
  `sequence_no` int(11) DEFAULT NULL,
  `status` tinyint(2) DEFAULT NULL,
  `eligibility_test` int(11) DEFAULT NULL,
  `study_version` int(11) DEFAULT NULL,
  `active` bit(1) DEFAULT NULL,
  `response_no_option` bit(1) DEFAULT NULL,
  `response_yes_option` bit(1) DEFAULT NULL,
  `is_used` char(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `eligibility_id_idx` (`eligibility_id`),
  CONSTRAINT `FK_eligibility_id` FOREIGN KEY (`eligibility_id`) REFERENCES `eligibility` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=937 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.eligibility_test_response
DROP TABLE IF EXISTS `eligibility_test_response`;
CREATE TABLE IF NOT EXISTS `eligibility_test_response` (
  `response_id` int(11) NOT NULL,
  `eligibility_test_id` int(11) DEFAULT NULL,
  `response_option` varchar(500) DEFAULT NULL,
  `pass_fail` varchar(20) DEFAULT NULL,
  `destination_question` int(11) DEFAULT NULL,
  `study_version` int(11) DEFAULT NULL,
  PRIMARY KEY (`response_id`),
  KEY `destination_question_idx` (`destination_question`),
  KEY `eligibility_test_id_idx` (`eligibility_test_id`),
  CONSTRAINT `destination_question` FOREIGN KEY (`destination_question`) REFERENCES `eligibility_test` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `eligibility_test_id` FOREIGN KEY (`eligibility_test_id`) REFERENCES `eligibility_test` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.enrollment_token
DROP TABLE IF EXISTS `enrollment_token`;
CREATE TABLE IF NOT EXISTS `enrollment_token` (
  `token_id` int(11) NOT NULL AUTO_INCREMENT,
  `enrollment_token` varchar(256) DEFAULT NULL,
  `created_on` datetime DEFAULT NULL,
  PRIMARY KEY (`token_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.form
DROP TABLE IF EXISTS `form`;
CREATE TABLE IF NOT EXISTS `form` (
  `form_id` int(11) NOT NULL AUTO_INCREMENT,
  `form_order` int(11) DEFAULT NULL,
  `created_by` int(11) DEFAULT NULL,
  `created_on` varchar(255) DEFAULT NULL,
  `modified_by` int(11) DEFAULT NULL,
  `modified_on` varchar(255) DEFAULT NULL,
  `question_type` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `active` bit(1) DEFAULT NULL,
  `study_version` int(11) DEFAULT NULL,
  PRIMARY KEY (`form_id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.form_mapping
DROP TABLE IF EXISTS `form_mapping`;
CREATE TABLE IF NOT EXISTS `form_mapping` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `form_id` int(11) DEFAULT NULL,
  `question_id` int(11) DEFAULT NULL,
  `sequence_no` int(11) DEFAULT NULL,
  `active` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.gateway_info
DROP TABLE IF EXISTS `gateway_info`;
CREATE TABLE IF NOT EXISTS `gateway_info` (
  `id` int(11) NOT NULL,
  `video_url` varchar(200) DEFAULT NULL,
  `email_inbox_address` varchar(100) DEFAULT NULL,
  `fda_website_url` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.gateway_welcome_info
DROP TABLE IF EXISTS `gateway_welcome_info`;
CREATE TABLE IF NOT EXISTS `gateway_welcome_info` (
  `id` int(11) NOT NULL,
  `app_title` varchar(100) DEFAULT NULL,
  `description` longtext,
  `image_path` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.groups
DROP TABLE IF EXISTS `groups`;
CREATE TABLE IF NOT EXISTS `groups` (
  `id` int(11) NOT NULL,
  `group_name` varchar(100) DEFAULT NULL,
  `group_created_on` datetime DEFAULT NULL,
  `group_created_by` int(11) DEFAULT NULL,
  `study_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `study_id_idx` (`study_id`),
  CONSTRAINT `FK_study_group_id` FOREIGN KEY (`study_id`) REFERENCES `studies` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.group_step_mapping
DROP TABLE IF EXISTS `group_step_mapping`;
CREATE TABLE IF NOT EXISTS `group_step_mapping` (
  `id` int(11) NOT NULL,
  `group_id` int(11) DEFAULT NULL,
  `step_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `group_id_idx` (`group_id`),
  KEY `step_id_idx` (`step_id`),
  CONSTRAINT `FK_group_step_mapping_id` FOREIGN KEY (`group_id`) REFERENCES `groups` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `step_id` FOREIGN KEY (`step_id`) REFERENCES `questionnaires_steps` (`step_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.health_kit_keys_info
DROP TABLE IF EXISTS `health_kit_keys_info`;
CREATE TABLE IF NOT EXISTS `health_kit_keys_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `category` varchar(255) DEFAULT NULL,
  `display_name` varchar(255) DEFAULT NULL,
  `key_text` varchar(255) DEFAULT NULL,
  `result_type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=75 DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.instructions
DROP TABLE IF EXISTS `instructions`;
CREATE TABLE IF NOT EXISTS `instructions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `instruction_title` varchar(250) DEFAULT NULL,
  `instruction_text` varchar(2500) DEFAULT NULL,
  `button_text` varchar(150) DEFAULT NULL,
  `created_by` int(11) DEFAULT NULL,
  `created_on` datetime DEFAULT NULL,
  `modified_by` int(11) DEFAULT NULL,
  `modified_on` datetime DEFAULT NULL,
  `active` tinyint(4) DEFAULT NULL,
  `study_version` int(11) DEFAULT NULL,
  `status` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6218 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.legal_text
DROP TABLE IF EXISTS `legal_text`;
CREATE TABLE IF NOT EXISTS `legal_text` (
  `id` int(11) NOT NULL,
  `mobile_app_terms` longtext,
  `mobile_app_terms_modified_datetime` datetime DEFAULT NULL,
  `mobile_app_privacy_policy` longtext,
  `mobile_app_privacy_policy_modified_datetime` datetime DEFAULT NULL,
  `web_app_terms` longtext,
  `web_app_terms_modified_datetime` datetime DEFAULT NULL,
  `web_app_privacy_policy` longtext,
  `web_app_privacy_policy_modified_datetime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.line_chart
DROP TABLE IF EXISTS `line_chart`;
CREATE TABLE IF NOT EXISTS `line_chart` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `line_chartcol` varchar(45) DEFAULT NULL,
  `no_data_text` varchar(100) DEFAULT NULL,
  `show_ver_hor_line` tinyint(1) DEFAULT NULL,
  `x_axis_color` varchar(10) DEFAULT NULL,
  `y_axis_color` varchar(10) DEFAULT NULL,
  `animation_needed` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.line_chart_datasource
DROP TABLE IF EXISTS `line_chart_datasource`;
CREATE TABLE IF NOT EXISTS `line_chart_datasource` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `data_source_id` int(11) DEFAULT NULL,
  `plot_color` varchar(10) DEFAULT NULL,
  `line_chart_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_line_chart_datasource_line_chart` (`line_chart_id`),
  CONSTRAINT `FK_line_chart_datasource_line_chart` FOREIGN KEY (`line_chart_id`) REFERENCES `line_chart` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.line_chart_x_axis
DROP TABLE IF EXISTS `line_chart_x_axis`;
CREATE TABLE IF NOT EXISTS `line_chart_x_axis` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(50) DEFAULT NULL,
  `line_chart_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_line_chart_x_axis_line_chart` (`line_chart_id`),
  CONSTRAINT `FK_line_chart_x_axis_line_chart` FOREIGN KEY (`line_chart_id`) REFERENCES `line_chart` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.live_ active_task_data_collected_master
DROP TABLE IF EXISTS `live_ active_task_data_collected_master`;
CREATE TABLE IF NOT EXISTS `live_ active_task_data_collected_master` (
  `id` int(11) NOT NULL,
  `task_name` varchar(100) DEFAULT NULL,
  `data_collected` varchar(250) DEFAULT NULL COMMENT 'eg. Device motion, Pedometer, Location, Heart rate',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.live_active_task
DROP TABLE IF EXISTS `live_active_task`;
CREATE TABLE IF NOT EXISTS `live_active_task` (
  `id` int(11) NOT NULL,
  `live_task_description` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.live_active_task_details
DROP TABLE IF EXISTS `live_active_task_details`;
CREATE TABLE IF NOT EXISTS `live_active_task_details` (
  `id` int(11) NOT NULL,
  `live_active_task_id` int(11) DEFAULT NULL,
  `parameter` varchar(100) DEFAULT NULL,
  `parameter_display_name` varchar(100) DEFAULT NULL,
  `parameter_description` varchar(1000) DEFAULT NULL,
  `editable` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `live_active_task_id_idx` (`live_active_task_id`),
  CONSTRAINT `live_active_task_id` FOREIGN KEY (`live_active_task_id`) REFERENCES `live_active_task` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.live_active_task_master
DROP TABLE IF EXISTS `live_active_task_master`;
CREATE TABLE IF NOT EXISTS `live_active_task_master` (
  `id` int(11) NOT NULL,
  `category` varchar(100) DEFAULT NULL,
  `task_name` varchar(100) DEFAULT NULL,
  `parameter` varchar(100) DEFAULT NULL,
  `parameter_display_name` varchar(100) DEFAULT NULL,
  `Parameter_description` varchar(1000) DEFAULT NULL,
  `sequence_no` int(11) DEFAULT NULL,
  `editable` tinyint(1) DEFAULT NULL,
  `parameter_type` varchar(50) DEFAULT NULL COMMENT 'String / Number / Int',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.master_data
DROP TABLE IF EXISTS `master_data`;
CREATE TABLE IF NOT EXISTS `master_data` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `type` varchar(50) DEFAULT NULL,
  `terms_text` text,
  `privacy_policy_text` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.notification
DROP TABLE IF EXISTS `notification`;
CREATE TABLE IF NOT EXISTS `notification` (
  `notification_id` int(11) NOT NULL AUTO_INCREMENT,
  `notification_type` varchar(255) DEFAULT NULL,
  `study_id` int(11) DEFAULT NULL,
  `custom_study_id` varchar(255) DEFAULT NULL,
  `notification_subType` varchar(255) DEFAULT NULL,
  `is_anchor_date` tinyint(1) DEFAULT NULL,
  `resource_id` int(11) DEFAULT NULL,
  `created_by` int(11) DEFAULT NULL,
  `created_on` timestamp NULL DEFAULT NULL,
  `modified_by` int(11) DEFAULT NULL,
  `modified_on` timestamp NULL DEFAULT NULL,
  `schedule_date` date DEFAULT NULL,
  `schedule_time` time DEFAULT NULL,
  `notification_action` tinyint(1) DEFAULT NULL,
  `notification_done` tinyint(1) DEFAULT NULL,
  `notification_schedule_type` varchar(255) DEFAULT NULL,
  `notification_sent` tinyint(1) DEFAULT NULL,
  `notification_status` tinyint(1) DEFAULT NULL,
  `notification_text` varchar(1024) DEFAULT NULL,
  `study_version` int(11) DEFAULT NULL,
  `x_days` int(11) DEFAULT NULL,
  `questionnarie_id` int(11) DEFAULT NULL,
  `active_task_id` int(11) DEFAULT NULL,
  `app_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`notification_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1511 DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.notification_history
DROP TABLE IF EXISTS `notification_history`;
CREATE TABLE IF NOT EXISTS `notification_history` (
  `history_id` int(11) NOT NULL AUTO_INCREMENT,
  `notification_sent_date_time` varchar(50) DEFAULT NULL,
  `notification_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`history_id`),
  KEY `notification_history_id` (`notification_id`),
  CONSTRAINT `notification_history_id` FOREIGN KEY (`notification_id`) REFERENCES `notification` (`notification_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=165 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.participant_activities_status
DROP TABLE IF EXISTS `participant_activities_status`;
CREATE TABLE IF NOT EXISTS `participant_activities_status` (
  `id` int(11) NOT NULL,
  `participant_id` int(11) DEFAULT NULL,
  `study_id` int(11) DEFAULT NULL,
  `activity_id` int(11) DEFAULT NULL COMMENT 'Active Task Id / Survey Id',
  `activity_complete_id` int(11) DEFAULT NULL,
  `activity_type` varchar(50) DEFAULT NULL COMMENT 'Active Tasks + Questionnaires',
  PRIMARY KEY (`id`),
  KEY `study_id_idx` (`study_id`),
  KEY `participant_id_idx` (`participant_id`),
  CONSTRAINT `FK_participant_status_id` FOREIGN KEY (`participant_id`) REFERENCES `participant_details` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_study_parti_status_id` FOREIGN KEY (`study_id`) REFERENCES `studies` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.participant_details
DROP TABLE IF EXISTS `participant_details`;
CREATE TABLE IF NOT EXISTS `participant_details` (
  `id` int(11) NOT NULL,
  `first_name` varchar(100) DEFAULT NULL,
  `last_name` varchar(100) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `use_passcode` tinyint(1) DEFAULT NULL,
  `touch_id` tinyint(1) DEFAULT NULL,
  `local_notification_flag` tinyint(1) DEFAULT NULL,
  `remote_notification_flag` tinyint(1) DEFAULT NULL,
  `reminder_flag` tinyint(1) DEFAULT NULL,
  `status` int(11) DEFAULT NULL COMMENT 'Active / Deleted',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.participant_studies
DROP TABLE IF EXISTS `participant_studies`;
CREATE TABLE IF NOT EXISTS `participant_studies` (
  `id` int(11) NOT NULL,
  `participant_id` int(11) DEFAULT NULL,
  `study_id` int(11) DEFAULT NULL,
  `status` tinyint(1) DEFAULT NULL COMMENT 'started / Paused / Not Eligility / Quit ...',
  `bookmark` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `participant_id_idx` (`participant_id`),
  KEY `study_id_idx` (`study_id`),
  CONSTRAINT `FK_participant_id` FOREIGN KEY (`participant_id`) REFERENCES `participant_details` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_study_participant_studies_id` FOREIGN KEY (`study_id`) REFERENCES `studies` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.pie_chart
DROP TABLE IF EXISTS `pie_chart`;
CREATE TABLE IF NOT EXISTS `pie_chart` (
  `id` int(11) NOT NULL,
  `data_source` int(11) DEFAULT NULL COMMENT 'question id / active task id',
  `distribution_type` tinyint(1) DEFAULT NULL COMMENT 'U - Unique responses, P - Pre defined range',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.pie_chart_segments
DROP TABLE IF EXISTS `pie_chart_segments`;
CREATE TABLE IF NOT EXISTS `pie_chart_segments` (
  `id` int(11) NOT NULL,
  `min_range` int(11) DEFAULT NULL,
  `max_range` int(11) DEFAULT NULL,
  `display_name` varchar(100) DEFAULT NULL,
  `segment_color` varchar(10) DEFAULT NULL,
  `pie_chart_id` int(11) DEFAULT NULL,
  `data_type` varchar(100) DEFAULT NULL COMMENT 'Device Motion',
  `choose_data` varchar(100) DEFAULT NULL COMMENT 'Step count',
  PRIMARY KEY (`id`),
  KEY `pie_chart_id_idx` (`pie_chart_id`),
  CONSTRAINT `FK_pie_chart_id` FOREIGN KEY (`pie_chart_id`) REFERENCES `pie_chart` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.questionnaires
DROP TABLE IF EXISTS `questionnaires`;
CREATE TABLE IF NOT EXISTS `questionnaires` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `study_id` int(11) DEFAULT NULL,
  `frequency` varchar(30) DEFAULT NULL,
  `title` varchar(500) DEFAULT NULL,
  `study_lifetime_start` date DEFAULT NULL,
  `study_lifetime_end` date DEFAULT NULL,
  `short_title` varchar(255) DEFAULT NULL,
  `day_of_the_week` varchar(255) DEFAULT NULL,
  `repeat_questionnaire` int(11) DEFAULT '0',
  `created_by` int(11) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `modified_by` int(11) DEFAULT NULL,
  `modified_date` datetime DEFAULT NULL,
  `branching` bit(1) DEFAULT NULL,
  `active` bit(1) DEFAULT NULL,
  `study_version` int(11) DEFAULT NULL,
  `modifiedDate` varchar(255) DEFAULT NULL,
  `modifiedBy` varchar(255) DEFAULT NULL,
  `createdDate` varchar(255) DEFAULT NULL,
  `createdBy` varchar(255) DEFAULT NULL,
  `status` bit(1) DEFAULT NULL,
  `custom_study_id` varchar(255) DEFAULT NULL,
  `is_live` int(11) DEFAULT NULL,
  `version` float DEFAULT NULL,
  `is_Change` tinyint(1) DEFAULT NULL,
  `schedule_type` varchar(50) DEFAULT NULL,
  `anchor_date_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `study_id_idx` (`study_id`),
  CONSTRAINT `FK_quest_study_id` FOREIGN KEY (`study_id`) REFERENCES `studies` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=11093 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.questionnaires_custom_frequencies
DROP TABLE IF EXISTS `questionnaires_custom_frequencies`;
CREATE TABLE IF NOT EXISTS `questionnaires_custom_frequencies` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `frequency_start_date` date DEFAULT NULL,
  `frequency_end_date` date DEFAULT NULL,
  `frequency_time` time DEFAULT NULL,
  `questionnaires_id` int(11) DEFAULT NULL,
  `study_version` int(11) DEFAULT NULL,
  `is_used` char(1) DEFAULT NULL,
  `time_period_from_days` int(11) DEFAULT NULL,
  `time_period_to_days` int(11) DEFAULT NULL,
  `x_days_sign` bit(1) DEFAULT NULL,
  `y_days_sign` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=66 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.questionnaires_frequencies
DROP TABLE IF EXISTS `questionnaires_frequencies`;
CREATE TABLE IF NOT EXISTS `questionnaires_frequencies` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `questionnaires_id` int(11) DEFAULT NULL,
  `frequency_date` date DEFAULT NULL,
  `frequency_time` time DEFAULT NULL,
  `is_launch_study` tinyint(1) DEFAULT NULL,
  `is_study_life_time` tinyint(1) DEFAULT NULL,
  `repeat_questionnaire` int(11) DEFAULT NULL,
  `hours_intervals` int(11) DEFAULT NULL,
  `day_of_the_week` varchar(255) DEFAULT NULL,
  `study_version` int(11) DEFAULT NULL,
  `time_period_from_days` int(11) DEFAULT NULL,
  `time_period_to_days` int(11) DEFAULT NULL,
  `x_days_sign` bit(1) DEFAULT NULL,
  `y_days_sign` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `questionnaires_id_idx` (`questionnaires_id`),
  CONSTRAINT `FK_questionnaires_fre_id` FOREIGN KEY (`questionnaires_id`) REFERENCES `questionnaires` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=13711 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.questionnaires_steps
DROP TABLE IF EXISTS `questionnaires_steps`;
CREATE TABLE IF NOT EXISTS `questionnaires_steps` (
  `step_id` int(11) NOT NULL AUTO_INCREMENT,
  `questionnaires_id` int(11) DEFAULT NULL,
  `instruction_form_id` int(11) DEFAULT NULL COMMENT 'Instruction Id / Form Id',
  `step_short_title` varchar(255) DEFAULT NULL,
  `step_type` varchar(50) DEFAULT NULL COMMENT 'Instuction/Form/Question',
  `randomization` varchar(1) DEFAULT NULL COMMENT 'Y / N',
  `sequence_no` int(11) DEFAULT NULL,
  `id` int(11) DEFAULT NULL,
  `destination_step` int(11) DEFAULT NULL,
  `repeatable` varchar(255) DEFAULT NULL,
  `repeatable_text` varchar(255) DEFAULT NULL,
  `skiappable` varchar(255) DEFAULT NULL,
  `active` bit(1) DEFAULT NULL,
  `created_by` int(11) DEFAULT NULL,
  `created_on` varchar(255) DEFAULT NULL,
  `modified_by` int(11) DEFAULT NULL,
  `modified_on` varchar(255) DEFAULT NULL,
  `status` bit(1) DEFAULT NULL,
  `study_version` int(11) DEFAULT NULL,
  PRIMARY KEY (`step_id`),
  KEY `questionnaires_id_idx` (`questionnaires_id`),
  CONSTRAINT `FK_questionnaires_qsteps_id` FOREIGN KEY (`questionnaires_id`) REFERENCES `questionnaires` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=60022 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.questions
DROP TABLE IF EXISTS `questions`;
CREATE TABLE IF NOT EXISTS `questions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `active` bit(1) DEFAULT NULL,
  `add_line_chart` varchar(255) DEFAULT NULL,
  `allow_rollback_chart` varchar(255) DEFAULT NULL,
  `chart_title` varchar(255) DEFAULT NULL,
  `created_by` int(11) DEFAULT NULL,
  `created_on` varchar(255) DEFAULT NULL,
  `description` varchar(512) DEFAULT NULL,
  `line_chart_timerange` varchar(255) DEFAULT NULL,
  `modified_by` int(11) DEFAULT NULL,
  `modified_on` varchar(255) DEFAULT NULL,
  `question` varchar(512) DEFAULT NULL,
  `response_type` int(11) DEFAULT NULL,
  `short_title` varchar(255) DEFAULT NULL,
  `skippable` varchar(255) DEFAULT NULL,
  `stat_display_name` varchar(255) DEFAULT NULL,
  `stat_diaplay_units` varchar(255) DEFAULT NULL,
  `stat_formula` int(11) DEFAULT NULL,
  `stat_short_name` varchar(255) DEFAULT NULL,
  `stat_type` int(11) DEFAULT NULL,
  `status` bit(1) DEFAULT NULL,
  `study_version` int(11) DEFAULT NULL,
  `use_anchor_date` bit(1) DEFAULT NULL,
  `use_stastic_data` varchar(255) DEFAULT NULL,
  `allow_healthkit` varchar(255) DEFAULT NULL,
  `healthkit_datatype` varchar(255) DEFAULT NULL,
  `anchor_date_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=84462 DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.questions_response_type
DROP TABLE IF EXISTS `questions_response_type`;
CREATE TABLE IF NOT EXISTS `questions_response_type` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `parameter_name` varchar(255) DEFAULT NULL,
  `parameter_value` varchar(255) DEFAULT NULL,
  `question_id` int(11) DEFAULT NULL,
  `study_version` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.question_condtion_branching
DROP TABLE IF EXISTS `question_condtion_branching`;
CREATE TABLE IF NOT EXISTS `question_condtion_branching` (
  `condition_id` int(11) NOT NULL AUTO_INCREMENT,
  `active` bit(1) DEFAULT NULL,
  `input_type` varchar(255) DEFAULT NULL,
  `input_type_value` varchar(255) DEFAULT NULL,
  `parent_sequence_no` int(11) DEFAULT NULL,
  `question_id` int(11) DEFAULT NULL,
  `sequence_no` int(11) DEFAULT NULL,
  PRIMARY KEY (`condition_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.question_responsetype_master_info
DROP TABLE IF EXISTS `question_responsetype_master_info`;
CREATE TABLE IF NOT EXISTS `question_responsetype_master_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `anchor_date` bit(1) DEFAULT NULL,
  `choice_based_branching` bit(1) DEFAULT NULL,
  `dashboard_allowed` bit(1) DEFAULT NULL,
  `data_type` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `formula_based_logic` bit(1) DEFAULT NULL,
  `healthkit_alternative` bit(1) DEFAULT NULL,
  `response_type` varchar(255) DEFAULT NULL,
  `response_type_code` varchar(255) DEFAULT NULL,
  `study_version` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.reference_tables
DROP TABLE IF EXISTS `reference_tables`;
CREATE TABLE IF NOT EXISTS `reference_tables` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `str_value` varchar(100) DEFAULT NULL,
  `category` varchar(100) DEFAULT NULL COMMENT 'Roles / Categories / Research Sponsors / Response formats ',
  `type` varchar(50) DEFAULT NULL COMMENT 'Pre-defined / Custom',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.rep_questions
DROP TABLE IF EXISTS `rep_questions`;
CREATE TABLE IF NOT EXISTS `rep_questions` (
  `id` int(11) NOT NULL,
  `short_title` varchar(200) DEFAULT NULL,
  `question` varchar(1000) DEFAULT NULL,
  `response_format` varchar(20) DEFAULT NULL,
  `type` varchar(50) DEFAULT NULL COMMENT 'Eligibility / Questionnaire',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.rep_resources
DROP TABLE IF EXISTS `rep_resources`;
CREATE TABLE IF NOT EXISTS `rep_resources` (
  `id` int(11) NOT NULL,
  `title` varchar(100) DEFAULT NULL,
  `text_or_pdf` tinyint(1) DEFAULT NULL,
  `rich_text` mediumtext,
  `pdf_url` varchar(200) DEFAULT NULL,
  `type` varchar(50) DEFAULT NULL COMMENT 'Study / Gateway',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.rep_response
DROP TABLE IF EXISTS `rep_response`;
CREATE TABLE IF NOT EXISTS `rep_response` (
  `id` int(11) NOT NULL,
  `questions_id` int(11) DEFAULT NULL,
  `response_option` varchar(500) DEFAULT NULL,
  `destination_question` int(11) DEFAULT NULL,
  `result` varchar(5) DEFAULT NULL COMMENT 'Pass / Fail',
  PRIMARY KEY (`id`),
  KEY `rep_questions_id_idx` (`questions_id`),
  KEY `destination_question_idx` (`destination_question`),
  CONSTRAINT `FK_destination_question` FOREIGN KEY (`destination_question`) REFERENCES `rep_questions` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_rep_questions_id` FOREIGN KEY (`questions_id`) REFERENCES `rep_questions` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.resources
DROP TABLE IF EXISTS `resources`;
CREATE TABLE IF NOT EXISTS `resources` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `study_id` int(11) DEFAULT NULL,
  `title` varchar(100) DEFAULT NULL,
  `text_or_pdf` tinyint(1) DEFAULT NULL,
  `rich_text` mediumtext,
  `pdf_url` varchar(200) DEFAULT NULL,
  `pdfName` varchar(200) DEFAULT NULL,
  `resource_visibility` tinyint(1) DEFAULT NULL,
  `time_period_from_days` int(11) DEFAULT NULL,
  `time_period_to_days` int(11) DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `resource_text` varchar(255) DEFAULT NULL,
  `action` tinyint(1) DEFAULT NULL,
  `study_protocol` tinyint(1) DEFAULT NULL,
  `created_by` int(11) DEFAULT NULL,
  `created_on` datetime DEFAULT NULL,
  `modified_by` int(11) DEFAULT NULL,
  `modified_on` datetime DEFAULT NULL,
  `status` tinyint(1) DEFAULT NULL,
  `pdf_name` varchar(255) DEFAULT NULL,
  `custom_study_id` varchar(255) DEFAULT NULL,
  `study_version` int(11) DEFAULT NULL,
  `resource_type` tinyint(1) DEFAULT NULL,
  `anchor_date` varchar(255) DEFAULT NULL,
  `x_days_sign` tinyint(1) DEFAULT '0',
  `y_days_sign` tinyint(1) DEFAULT '0',
  `sequence_no` int(11) DEFAULT NULL,
  `anchor_date_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `study_id_idx` (`study_id`),
  CONSTRAINT `FK_study_resources_id` FOREIGN KEY (`study_id`) REFERENCES `studies` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=10508 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.responses
DROP TABLE IF EXISTS `responses`;
CREATE TABLE IF NOT EXISTS `responses` (
  `id` int(11) NOT NULL,
  `question_id` int(11) DEFAULT NULL,
  `response_option` varchar(100) DEFAULT NULL,
  `destination_step` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `question_id_idx` (`question_id`),
  CONSTRAINT `question_response_id` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.response_sub_type_value
DROP TABLE IF EXISTS `response_sub_type_value`;
CREATE TABLE IF NOT EXISTS `response_sub_type_value` (
  `response_sub_type_value_id` int(11) NOT NULL AUTO_INCREMENT,
  `destination_step_id` int(11) DEFAULT NULL,
  `detail` varchar(255) DEFAULT NULL,
  `exclusive` varchar(50) DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL,
  `response_type_id` int(11) DEFAULT NULL,
  `selected_image` varchar(255) DEFAULT NULL,
  `study_version` int(11) DEFAULT NULL,
  `text` varchar(255) DEFAULT NULL,
  `value` varchar(255) DEFAULT NULL,
  `active` bit(1) DEFAULT NULL,
  `image_content` tinyblob,
  `selected_image_content` longblob,
  `description` varchar(255) DEFAULT NULL,
  `operator` varchar(255) DEFAULT NULL,
  `value_of_x` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`response_sub_type_value_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1368 DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.response_type_master
DROP TABLE IF EXISTS `response_type_master`;
CREATE TABLE IF NOT EXISTS `response_type_master` (
  `id` int(11) NOT NULL,
  `response_type_option` varchar(100) DEFAULT NULL COMMENT 'question-scale / question-continuousScale / question-textScale\n\n',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.response_type_parameter_master
DROP TABLE IF EXISTS `response_type_parameter_master`;
CREATE TABLE IF NOT EXISTS `response_type_parameter_master` (
  `id` int(11) NOT NULL,
  `question_type` varchar(100) DEFAULT NULL COMMENT 'question-scale / question-continuousScale / question-textScale .. etc',
  `parameter` varchar(100) DEFAULT NULL COMMENT 'maxValue / minValue / default',
  `parameter_type` varchar(100) DEFAULT NULL COMMENT 'Int / Boolean / String / Number',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.response_type_value
DROP TABLE IF EXISTS `response_type_value`;
CREATE TABLE IF NOT EXISTS `response_type_value` (
  `response_type_id` int(11) NOT NULL AUTO_INCREMENT,
  `questions_response_type_id` int(11) DEFAULT NULL,
  `active` tinyint(4) DEFAULT NULL,
  `default_date` varchar(255) DEFAULT NULL,
  `default_value` varchar(255) DEFAULT NULL,
  `image_size` varchar(255) DEFAULT NULL,
  `invalid_message` varchar(255) DEFAULT NULL,
  `max_date` varchar(255) DEFAULT NULL,
  `max_desc` varchar(255) DEFAULT NULL,
  `max_fraction_digits` int(11) DEFAULT NULL,
  `max_image` varchar(255) DEFAULT NULL,
  `max_length` int(50) DEFAULT NULL,
  `max_value` varchar(50) DEFAULT NULL,
  `measurement_system` varchar(255) DEFAULT NULL,
  `min_date` varchar(255) DEFAULT NULL,
  `min_desc` varchar(255) DEFAULT NULL,
  `min_image` varchar(255) DEFAULT NULL,
  `min_value` varchar(50) DEFAULT NULL,
  `multiple_lines` bit(1) DEFAULT NULL,
  `placeholder` varchar(255) DEFAULT NULL,
  `selection_style` varchar(255) DEFAULT NULL,
  `step` int(11) DEFAULT NULL,
  `study_version` int(11) DEFAULT NULL,
  `style` varchar(255) DEFAULT NULL,
  `text_choices` varchar(255) DEFAULT NULL,
  `unit` varchar(255) DEFAULT NULL,
  `use_current_location` bit(1) DEFAULT NULL,
  `validation_regex` text,
  `vertical` bit(1) DEFAULT NULL,
  `defalut_time` varchar(255) DEFAULT NULL,
  `formula_based_logic` varchar(255) DEFAULT NULL,
  `validation_characters` varchar(255) DEFAULT NULL,
  `validation_condition` varchar(255) DEFAULT NULL,
  `validation_except_text` text,
  `condition_formula` varchar(255) DEFAULT NULL,
  `other_description` varchar(255) DEFAULT NULL,
  `other_destination_step_id` int(11) DEFAULT NULL,
  `other_exclusive` varchar(255) DEFAULT NULL,
  `other_include_text` varchar(255) DEFAULT NULL,
  `other_participant_fill` varchar(255) DEFAULT NULL,
  `other_placeholder_text` varchar(255) DEFAULT NULL,
  `other_text` varchar(255) DEFAULT NULL,
  `other_type` varchar(255) DEFAULT NULL,
  `other_value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`response_type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=436 DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.roles
DROP TABLE IF EXISTS `roles`;
CREATE TABLE IF NOT EXISTS `roles` (
  `role_id` int(11) NOT NULL AUTO_INCREMENT,
  `role_name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.start_complete_step
DROP TABLE IF EXISTS `start_complete_step`;
CREATE TABLE IF NOT EXISTS `start_complete_step` (
  `id` int(11) NOT NULL,
  `start_complete_step` varchar(50) DEFAULT NULL COMMENT 'start / complete',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.statistics
DROP TABLE IF EXISTS `statistics`;
CREATE TABLE IF NOT EXISTS `statistics` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `short_title` varchar(100) DEFAULT NULL,
  `display_name` varchar(100) DEFAULT NULL,
  `stat_type` varchar(100) DEFAULT NULL,
  `display_unit` varchar(100) DEFAULT NULL,
  `formula` varchar(45) DEFAULT NULL,
  `data_source` int(11) DEFAULT NULL,
  `time_range` varchar(50) DEFAULT NULL,
  `custom` tinyint(1) DEFAULT NULL,
  `custom_start` datetime DEFAULT NULL,
  `custom_end` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.statistic_master_images
DROP TABLE IF EXISTS `statistic_master_images`;
CREATE TABLE IF NOT EXISTS `statistic_master_images` (
  `statistic_image_id` int(11) NOT NULL AUTO_INCREMENT,
  `value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`statistic_image_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.studies
DROP TABLE IF EXISTS `studies`;
CREATE TABLE IF NOT EXISTS `studies` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `custom_study_id` varchar(50) DEFAULT NULL,
  `name` varchar(200) DEFAULT NULL,
  `full_name` varchar(250) DEFAULT NULL,
  `type` varchar(20) DEFAULT NULL,
  `platform` varchar(20) DEFAULT NULL,
  `category` varchar(200) DEFAULT NULL,
  `research_sponsor` varchar(200) DEFAULT NULL,
  `data_partner` varchar(200) DEFAULT NULL,
  `tentative_duration` int(11) DEFAULT NULL,
  `tentative_duration_weekmonth` varchar(20) DEFAULT NULL,
  `description` longtext,
  `enrolling_participants` varchar(3) DEFAULT NULL,
  `retain_participant` varchar(50) DEFAULT NULL,
  `allow_rejoin` varchar(3) DEFAULT NULL,
  `irb_review` varchar(3) DEFAULT NULL,
  `inbox_email_address` varchar(500) DEFAULT NULL,
  `created_by` int(11) DEFAULT NULL,
  `created_on` datetime DEFAULT NULL,
  `modified_by` int(11) DEFAULT NULL,
  `modified_on` datetime DEFAULT NULL,
  `status` tinytext,
  `sequence_number` varchar(255) DEFAULT NULL,
  `thumbnail_image` varchar(255) DEFAULT NULL,
  `media_link` varchar(500) DEFAULT NULL,
  `allow_rejoin_text` varchar(255) DEFAULT NULL,
  `study_website` varchar(255) DEFAULT NULL,
  `study_tagline` varchar(255) DEFAULT NULL,
  `study_version` int(11) DEFAULT NULL,
  `study_lunched_date` varchar(255) DEFAULT NULL,
  `study_pre_active_flag` char(1) DEFAULT NULL,
  `has_activity_draft` int(11) DEFAULT NULL,
  `has_consent_draft` int(11) DEFAULT NULL,
  `has_study_draft` int(11) DEFAULT NULL,
  `is_live` int(11) DEFAULT NULL,
  `version` float DEFAULT NULL,
  `has_activitetask_draft` int(11) DEFAULT NULL,
  `has_questionnaire_draft` int(11) DEFAULT NULL,
  `enrollmentdate_as_anchordate` char(1) DEFAULT NULL,
  `app_id` varchar(255) DEFAULT NULL,
  `org_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1063 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.study_activity_version
DROP TABLE IF EXISTS `study_activity_version`;
CREATE TABLE IF NOT EXISTS `study_activity_version` (
  `study_activity_id` int(11) NOT NULL AUTO_INCREMENT,
  `activity_id` int(11) DEFAULT NULL,
  `activity_type` varchar(255) DEFAULT NULL,
  `activity_version` float DEFAULT NULL,
  `custom_study_id` varchar(255) DEFAULT NULL,
  `short_title` varchar(255) DEFAULT NULL,
  `study_version` float DEFAULT NULL,
  PRIMARY KEY (`study_activity_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8095 DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.study_checklist
DROP TABLE IF EXISTS `study_checklist`;
CREATE TABLE IF NOT EXISTS `study_checklist` (
  `checklist_id` int(10) NOT NULL AUTO_INCREMENT,
  `study_id` int(10) DEFAULT NULL,
  `checkbox1` tinyint(4) DEFAULT NULL,
  `checkbox2` tinyint(4) DEFAULT NULL,
  `checkbox3` tinyint(4) DEFAULT NULL,
  `checkbox4` tinyint(4) DEFAULT NULL,
  `checkbox5` tinyint(4) DEFAULT NULL,
  `checkbox6` tinyint(4) DEFAULT NULL,
  `checkbox7` tinyint(4) DEFAULT NULL,
  `checkbox8` tinyint(4) DEFAULT NULL,
  `checkbox9` tinyint(4) DEFAULT NULL,
  `checkbox10` tinyint(4) DEFAULT NULL,
  `study_version` varchar(255) DEFAULT NULL,
  `created_by` int(11) DEFAULT NULL,
  `created_on` varchar(255) DEFAULT NULL,
  `modified_by` int(11) DEFAULT NULL,
  `modified_on` varchar(255) DEFAULT NULL,
  `custom_study_id` varchar(255) DEFAULT NULL,
  `checkbox11` bit(1) DEFAULT NULL,
  `checkbox12` bit(1) DEFAULT NULL,
  PRIMARY KEY (`checklist_id`),
  KEY `FK1_study_checklist_id` (`study_id`),
  CONSTRAINT `FK1_study_checklist_id` FOREIGN KEY (`study_id`) REFERENCES `studies` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=76 DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.study_page
DROP TABLE IF EXISTS `study_page`;
CREATE TABLE IF NOT EXISTS `study_page` (
  `page_id` int(11) NOT NULL AUTO_INCREMENT,
  `study_id` int(11) DEFAULT NULL,
  `title` varchar(200) DEFAULT NULL,
  `image_path` varchar(100) DEFAULT NULL,
  `description` longtext,
  `created_on` datetime DEFAULT NULL,
  `modified_on` datetime DEFAULT NULL,
  `created_by` int(11) DEFAULT NULL,
  `modified_by` int(11) DEFAULT NULL,
  `study_version` int(11) DEFAULT NULL,
  PRIMARY KEY (`page_id`),
  KEY `study_id_idx` (`study_id`),
  CONSTRAINT `study_id` FOREIGN KEY (`study_id`) REFERENCES `studies` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=1866 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.study_permission
DROP TABLE IF EXISTS `study_permission`;
CREATE TABLE IF NOT EXISTS `study_permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `study_id` int(11) DEFAULT NULL,
  `view_permission` tinyint(1) DEFAULT NULL COMMENT '0 - View only, 1 - View and Edit',
  `project_lead` varchar(11) DEFAULT NULL COMMENT 'Y - Yes, N -  No(userId we need to store)',
  `delFlag` tinyint(1) DEFAULT NULL COMMENT '0 - inactive, 1 - active',
  PRIMARY KEY (`id`),
  KEY `user_id_idx` (`user_id`),
  KEY `study_id_idx` (`study_id`),
  CONSTRAINT `FK_study_id` FOREIGN KEY (`study_id`) REFERENCES `studies` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=4700 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.study_sequence
DROP TABLE IF EXISTS `study_sequence`;
CREATE TABLE IF NOT EXISTS `study_sequence` (
  `study_sequence_id` int(11) NOT NULL AUTO_INCREMENT,
  `study_id` int(11) DEFAULT NULL,
  `actions` char(1) DEFAULT NULL,
  `basic_info` char(1) DEFAULT NULL,
  `check_list` char(1) DEFAULT NULL,
  `comprehension_test` char(1) DEFAULT NULL,
  `consent_edu_info` char(1) DEFAULT NULL,
  `e_consent` char(1) DEFAULT NULL,
  `eligibility` char(1) DEFAULT NULL,
  `miscellaneous_branding` char(1) DEFAULT NULL,
  `miscellaneous_notification` char(1) DEFAULT NULL,
  `miscellaneous_resources` char(1) DEFAULT NULL,
  `over_view` char(1) DEFAULT NULL,
  `setting_admins` char(1) DEFAULT NULL,
  `study_dashboard_chart` char(1) DEFAULT NULL,
  `study_dashboard_stats` char(1) DEFAULT NULL,
  `study_exc_active_task` char(1) DEFAULT NULL,
  `study_exc_questionnaries` char(1) DEFAULT NULL,
  `study_version` int(11) DEFAULT NULL,
  PRIMARY KEY (`study_sequence_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1063 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.study_version
DROP TABLE IF EXISTS `study_version`;
CREATE TABLE IF NOT EXISTS `study_version` (
  `version_id` int(11) NOT NULL AUTO_INCREMENT,
  `activity_version` float DEFAULT NULL,
  `custom_study_id` varchar(255) DEFAULT NULL,
  `study_version` float DEFAULT NULL,
  `consent_version` float DEFAULT NULL,
  PRIMARY KEY (`version_id`)
) ENGINE=InnoDB AUTO_INCREMENT=979 DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.users
DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `first_name` varchar(100) DEFAULT NULL,
  `last_name` varchar(100) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `password` varchar(512) DEFAULT NULL,
  `phone_number` varchar(20) DEFAULT NULL,
  `role_id` int(11) DEFAULT NULL,
  `created_by` int(11) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `modified_by` int(11) DEFAULT NULL,
  `modified_date` datetime DEFAULT NULL,
  `status` tinyint(1) DEFAULT NULL,
  `access_code` varchar(255) DEFAULT NULL,
  `accountNonExpired` tinyint(4) DEFAULT NULL,
  `accountNonLocked` tinyint(4) DEFAULT NULL,
  `created_date_time` varchar(255) DEFAULT NULL,
  `credentialsNonExpired` tinyint(4) DEFAULT NULL,
  `modified_date_time` varchar(255) DEFAULT NULL,
  `password_expairded_datetime` varchar(255) DEFAULT NULL,
  `security_token` varchar(255) DEFAULT NULL,
  `token_expiry_date` varchar(255) DEFAULT NULL,
  `token_used` tinyint(4) DEFAULT NULL,
  `force_logout` char(1) DEFAULT NULL,
  `user_login_datetime` varchar(255) DEFAULT NULL,
  `email_changed` tinyint(1) unsigned zerofill NOT NULL DEFAULT '0',
  PRIMARY KEY (`user_id`),
  KEY `role_id_idx` (`role_id`),
  CONSTRAINT `role_id` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=58 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.users_password_history
DROP TABLE IF EXISTS `users_password_history`;
CREATE TABLE IF NOT EXISTS `users_password_history` (
  `password_history_id` int(11) NOT NULL AUTO_INCREMENT,
  `created_date` varchar(255) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `password` varchar(512) DEFAULT NULL,
  PRIMARY KEY (`password_history_id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.users_temp
DROP TABLE IF EXISTS `users_temp`;
CREATE TABLE IF NOT EXISTS `users_temp` (
  `user_temp_id` int(11) NOT NULL AUTO_INCREMENT,
  `access_code` varchar(255) DEFAULT NULL,
  `accountNonExpired` bit(1) DEFAULT NULL,
  `accountNonLocked` bit(1) DEFAULT NULL,
  `asp_hi_id` int(11) DEFAULT NULL,
  `created_by` int(11) DEFAULT NULL,
  `created_date_time` varchar(255) DEFAULT NULL,
  `credentialsNonExpired` bit(1) DEFAULT NULL,
  `status` bit(1) DEFAULT NULL,
  `fax_number` varchar(255) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `modified_by` int(11) DEFAULT NULL,
  `modified_date_time` varchar(255) DEFAULT NULL,
  `password_expairded_datetime` varchar(255) DEFAULT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  `security_token` varchar(255) DEFAULT NULL,
  `super_admin_id` int(11) DEFAULT NULL,
  `token_expiry_date` varchar(255) DEFAULT NULL,
  `token_used` bit(1) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `user_type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`user_temp_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.user_attempts
DROP TABLE IF EXISTS `user_attempts`;
CREATE TABLE IF NOT EXISTS `user_attempts` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `attempts` int(11) DEFAULT NULL,
  `last_modified` varchar(255) DEFAULT NULL,
  `email_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.user_permissions
DROP TABLE IF EXISTS `user_permissions`;
CREATE TABLE IF NOT EXISTS `user_permissions` (
  `permission_id` int(11) NOT NULL AUTO_INCREMENT,
  `permissions` varchar(45) NOT NULL,
  PRIMARY KEY (`permission_id`),
  UNIQUE KEY `permission_id` (`permission_id`),
  UNIQUE KEY `permissions` (`permissions`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.user_permissions_users
DROP TABLE IF EXISTS `user_permissions_users`;
CREATE TABLE IF NOT EXISTS `user_permissions_users` (
  `user_permissions_permission_id` int(11) NOT NULL,
  `users_user_id` int(11) NOT NULL,
  PRIMARY KEY (`user_permissions_permission_id`,`users_user_id`),
  KEY `FK3CB60B1986B4070C` (`user_permissions_permission_id`),
  KEY `FK3CB60B19B9441C99` (`users_user_id`),
  KEY `FK3CB60B19B6DE1B0C` (`user_permissions_permission_id`),
  KEY `FK3CB60B1991B38899` (`users_user_id`),
  CONSTRAINT `FK3CB60B1986B4070C` FOREIGN KEY (`user_permissions_permission_id`) REFERENCES `user_permissions` (`permission_id`),
  CONSTRAINT `FK3CB60B1991B38899` FOREIGN KEY (`users_user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `FK3CB60B19B6DE1B0C` FOREIGN KEY (`user_permissions_permission_id`) REFERENCES `user_permissions` (`permission_id`),
  CONSTRAINT `FK3CB60B19B9441C99` FOREIGN KEY (`users_user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.user_permission_mapping
DROP TABLE IF EXISTS `user_permission_mapping`;
CREATE TABLE IF NOT EXISTS `user_permission_mapping` (
  `user_id` int(11) NOT NULL,
  `permission_id` int(11) NOT NULL,
  PRIMARY KEY (`user_id`,`permission_id`),
  KEY `FKFEC4BF5294586FD0` (`user_id`),
  KEY `FKFEC4BF528CE62AFB` (`permission_id`),
  KEY `FKFEC4BF526CC7DBD0` (`user_id`),
  KEY `FKFEC4BF52BD103EFB` (`permission_id`),
  CONSTRAINT `FKFEC4BF526CC7DBD0` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `FKFEC4BF528CE62AFB` FOREIGN KEY (`permission_id`) REFERENCES `user_permissions` (`permission_id`),
  CONSTRAINT `FKFEC4BF5294586FD0` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `FKFEC4BF52BD103EFB` FOREIGN KEY (`permission_id`) REFERENCES `user_permissions` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table fda_hphc.version_info
DROP TABLE IF EXISTS `version_info`;
CREATE TABLE IF NOT EXISTS `version_info` (
  `version_info_id` int(11) NOT NULL AUTO_INCREMENT,
  `android` varchar(255) DEFAULT NULL,
  `ios` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`version_info_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping data for table fda_hphc.activetask_formula: ~4 rows (approximately)
/*!40000 ALTER TABLE `activetask_formula` DISABLE KEYS */;
INSERT INTO `activetask_formula` (`activetask_formula_id`, `value`, `formula`) VALUES
	(1, 'Summation of responses gathered over specified time range', 'Summation'),
	(2, 'Average of responses gathered over specified time range', 'Average'),
	(3, 'Maximum of a set of responses gathered over specified time range', 'Maximum'),
	(4, 'Minimum of a set of responses gathered over specified time range', 'Minimum');
/*!40000 ALTER TABLE `activetask_formula` ENABLE KEYS */;

-- Dumping data for table fda_hphc.active_task_list: ~3 rows (approximately)
/*!40000 ALTER TABLE `active_task_list` DISABLE KEYS */;
INSERT INTO `active_task_list` (`active_task_list_id`, `task_name`, `type`) VALUES
	(1, 'Fetal Kick Counter', 'fetalKickCounter'),
	(2, 'Tower Of Hanoi', 'towerOfHanoi'),
	(3, 'Spatial Span Memory', 'spatialSpanMemory');
/*!40000 ALTER TABLE `active_task_list` ENABLE KEYS */;

-- Dumping data for table fda_hphc.active_task_master_attribute: ~16 rows (approximately)
/*!40000 ALTER TABLE `active_task_master_attribute` DISABLE KEYS */;
INSERT INTO `active_task_master_attribute` (`active_task_master_attr_id`, `task_type_id`, `order_by`, `attribute_type`, `attribute_name`, `display_name`, `attribute_data_type`, `add_to_dashboard`, `task_type`, `study_version`) VALUES
	(1, 1, 1, 'configure_type', 'duration_fetal', 'Number of kicks recorded', 'time_picker', 'Y', NULL, NULL),
	(2, 1, 2, 'configure_type', 'duration_kick_count_fetal', 'Number of kicks to be recorded (N)', 'numeric', 'Y', NULL, NULL),
	(3, 1, 3, 'result_type', 'number_of_kicks_recorded_fetal', 'Time taken to record N kicks (in minutes)', 'numeric', 'Y', NULL, NULL),
	(4, 2, 1, 'configure_type', 'number_of_disks_tower', 'Number of Disks', 'numeric', 'Y', NULL, NULL),
	(5, 2, 2, 'result_type', 'puzzle_solved_unsolved_tower', 'Puzzle Solved/Unsolved', 'boolean', 'N', NULL, NULL),
	(6, 2, 3, 'result_type', 'number_of_moves_tower', 'Number of Moves', 'numeric', 'Y', NULL, NULL),
	(7, 3, 1, 'configure_type', 'Initial_Span_spatial', 'Initial Span', 'numeric', 'Y', NULL, NULL),
	(8, 3, 2, 'configure_type', 'Minimum_Span_spatial', 'Minimum Span', 'numeric', 'Y', NULL, NULL),
	(9, 3, 3, 'configure_type', 'Maximum_Span_spatial', 'Maximum Span', 'numeric', 'Y', NULL, NULL),
	(10, 3, 4, 'configure_type', 'Play_Speed_spatial', 'Play Speed', 'single select', 'Y', NULL, NULL),
	(11, 3, 5, 'configure_type', 'Maximum_Tests_spatial', 'Maximum Tests', 'numeric', 'Y', NULL, NULL),
	(12, 3, 6, 'configure_type', 'Maximum_Consecutive_Failuress_spatial', 'Maximum Consecutive Failures', 'numeric', 'Y', NULL, NULL),
	(13, 3, 7, 'configure_type', 'Require_reversal_spatial', 'Require Reversal?', 'boolean', 'Y', NULL, NULL),
	(14, 3, 8, 'result_type', 'Score_spatial', 'Score', 'numeric', 'Y', NULL, NULL),
	(15, 3, 9, 'result_type', 'Number_of_Games_spatial', 'Number of Games', 'numeric', 'Y', NULL, NULL),
	(16, 3, 10, 'result_type', 'Number_of_Failures_spatial', 'Number of Failures', 'numeric', 'Y', NULL, NULL);
/*!40000 ALTER TABLE `active_task_master_attribute` ENABLE KEYS */;

-- Dumping data for table fda_hphc.consent_master_info: ~8 rows (approximately)
/*!40000 ALTER TABLE `consent_master_info` DISABLE KEYS */;
INSERT INTO `consent_master_info` (`id`, `title`, `type`, `code`, `study_version`) VALUES
	(2, 'Overview', 'ResearchKit', 'overview', NULL),
	(3, 'Data gathering', 'ResearchKit', 'dataGathering', NULL),
	(4, 'Privacy ', 'ResearchKit', 'privacy', NULL),
	(5, 'Data use', 'ResearchKit', 'dataUse', NULL),
	(6, 'Time commitment', 'ResearchKit', 'timeCommitment', NULL),
	(7, 'Surveys', 'ResearchKit', 'studySurvey', NULL),
	(8, 'Tasks ', 'ResearchKit', 'studyTasks', NULL),
	(9, 'Withdrawal', 'ResearchKit', 'withdrawing', NULL);
/*!40000 ALTER TABLE `consent_master_info` ENABLE KEYS */;

-- Dumping data for table fda_hphc.health_kit_keys_info: ~73 rows (approximately)
/*!40000 ALTER TABLE `health_kit_keys_info` DISABLE KEYS */;
INSERT INTO `health_kit_keys_info` (`id`, `category`, `display_name`, `key_text`, `result_type`) VALUES
	(1, 'Body\r\nMeasurements', 'Body Mass Index', 'HKQuantityTypeIdentifierBodyMassIndex', 'Count'),
	(2, 'Body\r\nMeasurements', 'Body Fat Percentage', 'HKQuantityTypeIdentifierBodyFatPercentage', 'Percentage'),
	(3, 'Body\r\nMeasurements', 'Height', 'HKQuantityTypeIdentifierHeight', 'Length'),
	(4, 'Body\r\nMeasurements', 'Body Mass', 'HKQuantityTypeIdentifierBodyMass', 'Mass'),
	(5, 'Body\r\nMeasurements', 'Lean Body Mass', 'HKQuantityTypeIdentifierLeanBodyMass', 'Mass'),
	(6, 'Fitness', 'Step Count', 'HKQuantityTypeIdentifierStepCount', 'Count'),
	(7, 'Fitness', 'Distance Walk/Run', 'HKQuantityTypeIdentifierDistanceWalkingRunning', 'Length'),
	(8, 'Fitness', 'Distance Cycling', 'HKQuantityTypeIdentifierDistanceCycling', 'Length'),
	(9, 'Fitness', 'Distance Wheelchair', 'HKQuantityTypeIdentifierDistanceWheelchair', 'Length'),
	(10, 'Fitness', 'Basal Energy Burned', 'HKQuantityTypeIdentifierBasalEnergyBurned', 'Energy'),
	(11, 'Fitness', 'Active Energy Burned', 'HKQuantityTypeIdentifierActiveEnergyBurned', 'Energy'),
	(12, 'Fitness', 'Flight Climbed', 'HKQuantityTypeIdentifierFlightsClimbed', 'Count'),
	(13, 'Fitness', 'Nike Fuel', 'HKQuantityTypeIdentifierNikeFuel', 'Count'),
	(14, 'Fitness', 'Exercise Time', 'HKQuantityTypeIdentifierAppleExerciseTime', 'Time'),
	(15, 'Fitness', 'Push Count', 'HKQuantityTypeIdentifierPushCount', 'Count'),
	(16, 'Fitness', 'Distance Swimming', 'HKQuantityTypeIdentifierDistanceSwimming', 'Length'),
	(17, 'Fitness', 'Swimming Stroke Count', 'HKQuantityTypeIdentifierSwimmingStrokeCount', 'Count'),
	(18, 'Vitals', 'Heart Rate', 'HKQuantityTypeIdentifierHeartRate', 'Count/Time'),
	(19, 'Vitals', 'Body Temperature', 'HKQuantityTypeIdentifierBodyTemperature', 'Temperature'),
	(20, 'Vitals', 'Basal Body Temperature', 'HKQuantityTypeIdentifierBasalBodyTemperature', 'Temperature'),
	(21, 'Vitals', 'Blood Pressure Systolic', 'HKQuantityTypeIdentifierBloodPressureSystolic', 'Pressure'),
	(23, 'Vitals', 'Blood Pressure Diastolic', 'HKQuantityTypeIdentifierBloodPressureDiastolic', 'Pressure'),
	(24, 'Vitals', 'Respiratory Rate', 'HKQuantityTypeIdentifierRespiratoryRate', 'Count/Time'),
	(25, 'Results', 'Oxygen Saturation', 'HKQuantityTypeIdentifierOxygenSaturation', 'Percentage'),
	(26, 'Results', 'Peripheral Perfusion Index', 'HKQuantityTypeIdentifierPeripheralPerfusionIndex', 'Percentage'),
	(27, 'Results', 'Blood Glucose', 'HKQuantityTypeIdentifierBloodGlucose', 'Mass/Volume'),
	(28, 'Results', 'Number of times fallen', 'HKQuantityTypeIdentifierNumberOfTimesFallen', 'Count'),
	(29, 'Results', 'Electrodermal Activity', 'HKQuantityTypeIdentifierElectrodermalActivity', 'Conductance'),
	(30, 'Results', 'Inhaler Usage', 'HKQuantityTypeIdentifierInhalerUsage', 'Count'),
	(31, 'Results', 'Blood Alcohol Count', 'HKQuantityTypeIdentifierBloodAlcoholContent', 'Percentage'),
	(32, 'Results', 'Forced Vital Capacity', 'HKQuantityTypeIdentifierForcedVitalCapacity', 'Volume'),
	(33, 'Results', 'Forced Expiratory Volume', 'HKQuantityTypeIdentifierForcedExpiratoryVolume1', 'Volume'),
	(34, 'Results', 'Peak Expiratory Flow Rate', 'HKQuantityTypeIdentifierPeakExpiratoryFlowRate', 'Volume/Time'),
	(35, 'Nutrition', 'Dietary Fat', 'HKQuantityTypeIdentifierDietaryFatTotal', 'Mass'),
	(36, 'Nutrition', 'Dietary Fat Polyunsaturated', 'HKQuantityTypeIdentifierDietaryFatPolyunsaturated', 'Mass'),
	(37, 'Nutrition', 'Dietary Fat Monounsaturated', 'HKQuantityTypeIdentifierDietaryFatMonounsaturated', 'Mass'),
	(38, 'Nutrition', 'Dietary Fat Saturated', 'HKQuantityTypeIdentifierDietaryFatSaturated', 'Mass'),
	(39, 'Nutrition', 'Dietary Cholestrol', 'HKQuantityTypeIdentifierDietaryCholesterol', 'Mass'),
	(40, 'Nutrition', 'Dietary Sodium', 'HKQuantityTypeIdentifierDietarySodium', 'Mass'),
	(41, 'Nutrition', 'Dietary Carbohydrate', 'HKQuantityTypeIdentifierDietaryCarbohydrates', 'Mass'),
	(42, 'Nutrition', 'Dietary Fiber', 'HKQuantityTypeIdentifierDietaryFiber', 'Mass'),
	(43, 'Nutrition', 'Dietary Sugar', 'HKQuantityTypeIdentifierDietarySugar', 'Mass'),
	(44, 'Nutrition', 'Dietary Energy Consumed', 'HKQuantityTypeIdentifierDietaryEnergyConsumed', 'Energy'),
	(45, 'Nutrition', 'Dietary Protein', 'HKQuantityTypeIdentifierDietaryProtein', 'Mass'),
	(46, 'Nutrition', 'Dietary Vitamin A', 'HKQuantityTypeIdentifierDietaryVitaminA', 'Mass'),
	(47, 'Nutrition', 'Dietary Vitamin B6', 'HKQuantityTypeIdentifierDietaryVitaminB6', 'Mass'),
	(48, 'Nutrition', 'Dietary Vitamin B12', 'HKQuantityTypeIdentifierDietaryVitaminB12', 'Mass'),
	(49, 'Nutrition', 'Dietary Vitamin C', 'HKQuantityTypeIdentifierDietaryVitaminC', 'Mass'),
	(50, 'Nutrition', 'Dietary Vitamin D', 'HKQuantityTypeIdentifierDietaryVitaminD', 'Mass'),
	(51, 'Nutrition', 'Dietary Vitamin E', 'HKQuantityTypeIdentifierDietaryVitaminE', 'Mass'),
	(52, 'Nutrition', 'Dietary Vitamin K', 'HKQuantityTypeIdentifierDietaryVitaminK', 'Mass'),
	(53, 'Nutrition', 'Dietary Calcium', 'HKQuantityTypeIdentifierDietaryCalcium', 'Mass'),
	(54, 'Nutrition', 'Dietary Iron', 'HKQuantityTypeIdentifierDietaryIron', 'Mass'),
	(55, 'Nutrition', 'Dietary Thiamin', 'HKQuantityTypeIdentifierDietaryThiamin', 'Mass'),
	(56, 'Nutrition', 'Dietary Riboflavin', 'HKQuantityTypeIdentifierDietaryRiboflavin', 'Mass'),
	(57, 'Nutrition', 'Dietary Niacin', 'HKQuantityTypeIdentifierDietaryNiacin', 'Mass'),
	(58, 'Nutrition', 'Dietary Folate', 'HKQuantityTypeIdentifierDietaryFolate', 'Mass'),
	(59, 'Nutrition', 'Dietary Biotin', 'HKQuantityTypeIdentifierDietaryBiotin', 'Mass'),
	(60, 'Nutrition', 'Dietary Pantothenic Acid', 'HKQuantityTypeIdentifierDietaryPantothenicAcid', 'Mass'),
	(61, 'Nutrition', 'Dietary Phosphorus', 'HKQuantityTypeIdentifierDietaryPhosphorus', 'Mass'),
	(62, 'Nutrition', 'Dietary Iodine', 'HKQuantityTypeIdentifierDietaryIodine', 'Mass'),
	(63, 'Nutrition', 'Dietary Magnesium', 'HKQuantityTypeIdentifierDietaryMagnesium', 'Mass'),
	(64, 'Nutrition', 'Dietary Zinc', 'HKQuantityTypeIdentifierDietaryZinc', 'Mass'),
	(65, 'Nutrition', 'Dietary Selenium', 'HKQuantityTypeIdentifierDietarySelenium', 'Mass'),
	(66, 'Nutrition', 'Dietary Copper', 'HKQuantityTypeIdentifierDietaryCopper', 'Mass'),
	(67, 'Nutrition', 'Dietary Manganese', 'HKQuantityTypeIdentifierDietaryManganese', 'Mass'),
	(68, 'Nutrition', 'Dietary Chromium', 'HKQuantityTypeIdentifierDietaryChromium', 'Mass'),
	(69, 'Nutrition', 'Dietary Molybdenum', 'HKQuantityTypeIdentifierDietaryMolybdenum', 'Mass'),
	(70, 'Nutrition', 'Dietary Chloride', 'HKQuantityTypeIdentifierDietaryChloride', 'Mass'),
	(71, 'Nutrition', 'Dietary Potassium', 'HKQuantityTypeIdentifierDietaryPotassium', 'Mass'),
	(72, 'Nutrition', 'Dietary Caffeine', 'HKQuantityTypeIdentifierDietaryCaffeine', 'Mass'),
	(73, 'Nutrition', 'Dietary Water', 'HKQuantityTypeIdentifierDietaryWater', 'Volume'),
	(74, 'Environment', 'UV Exposure', 'HKQuantityTypeIdentifierUVExposure', 'Count');
/*!40000 ALTER TABLE `health_kit_keys_info` ENABLE KEYS */;

-- Dumping data for table fda_hphc.question_responsetype_master_info: ~15 rows (approximately)
/*!40000 ALTER TABLE `question_responsetype_master_info` DISABLE KEYS */;
INSERT INTO `question_responsetype_master_info` (`id`, `anchor_date`, `choice_based_branching`, `dashboard_allowed`, `data_type`, `description`, `formula_based_logic`, `healthkit_alternative`, `response_type`, `response_type_code`, `study_version`) VALUES
	(1, b'0', b'0', b'1', 'Double', 'Represents a response format that includes a slider control.', b'1', b'0', 'Scale', 'scale', NULL),
	(2, b'0', b'0', b'1', 'Double', 'Represents a response format that lets participants select a value on a continuous scale.', b'1', b'0', 'Continuous Scale', 'continuousScale', NULL),
	(3, b'0', b'1', b'0', 'String', 'Represents a response format that includes a discrete slider control with a text label next to each step.', b'0', b'0', 'Text Scale', 'textScale', NULL),
	(4, b'0', b'0', b'0', 'String', 'Represents a response format that lets participants use a value picker to choose from a fixed set of text choices.', b'0', b'0', 'Value Picker', 'valuePicker', NULL),
	(5, b'0', b'1', b'0', 'String', 'Represents a response format that lets participants choose one image from a fixed set of images in a single choice question.', b'0', b'0', 'Image Choice', 'imageChoice', NULL),
	(6, b'0', b'1', b'0', 'String', 'Represents a response format that lets participants choose from a fixed set of text choices in a multiple or single choice question.', b'0', b'0', 'Text Choice', 'textChoice', NULL),
	(7, b'0', b'1', b'0', 'Boolean', 'Represents a response format that lets participants choose from Yes and No options', b'0', b'0', 'Boolean', 'boolean', NULL),
	(8, b'0', b'0', b'1', 'Double', 'Represents a numeric response format that participants enter using a numeric keyboard.', b'1', b'1', 'Numeric', 'numeric', NULL),
	(9, b'0', b'0', b'0', 'String; \'HH:mm:ss\'', 'Represents the response format for questions that require users to enter a time of day.', b'0', b'0', 'Time of the day', 'timeOfDay', NULL),
	(10, b'1', b'0', b'0', 'String; \'yyyy-MM-dd\'T\'HH:mm:ss.SSSZ\'', 'Represents the response format for questions that require users to enter a date, or a date and time.', b'0', b'0', 'Date', 'date', NULL),
	(11, b'0', b'0', b'0', 'String', 'Represents the response format for questions that collect a text response from the user.', b'0', b'0', 'Text', 'text', NULL),
	(12, b'0', b'0', b'0', 'String', 'Represents the response format for questions that collect an email response from the user.', b'0', b'0', 'Email', 'email', NULL),
	(13, b'0', b'0', b'1', 'Double', 'Represents the response format for questions that ask users to specify a time interval. This is suitable for time intervals up to 24 hours', b'1', b'0', 'Time interval', 'timeInterval', NULL),
	(14, b'0', b'0', b'1', 'Double', 'Represents the response format for questions that ask users to specify a height value.', b'1', b'1', 'Height', 'height', NULL),
	(15, b'0', b'0', b'0', 'String; (lat,long)', 'Represents the response format for questions that collect a location response from the user. Displays a map on screen.', b'0', b'0', 'Location', 'location', NULL);
/*!40000 ALTER TABLE `question_responsetype_master_info` ENABLE KEYS */;

-- Dumping data for table fda_hphc.reference_tables: ~12 rows (approximately)
/*!40000 ALTER TABLE `reference_tables` DISABLE KEYS */;
INSERT INTO `reference_tables` (`id`, `str_value`, `category`, `type`) VALUES
	(1, 'Biologics Safety', 'Categories', 'Pre-defined'),
	(2, 'Clinical Trials', 'Categories', 'Pre-defined'),
	(3, 'Cosmetics Safety', 'Categories', 'Pre-defined'),
	(4, 'Drug Safety', 'Categories', 'Pre-defined'),
	(5, 'Food Safety', 'Categories', 'Pre-defined'),
	(6, 'Medical Device Safety', 'Categories', 'Pre-defined'),
	(7, 'Observational Studies', 'Categories', 'Pre-defined'),
	(8, 'Public Health', 'Categories', 'Pre-defined'),
	(9, 'Radiation-Emitting Products', 'Categories', 'Pre-defined'),
	(10, 'Tobacco Use', 'Categories', 'Pre-defined'),
	(11, 'Kaiser Permanente Washington Health Research Institute', 'Data Partner', 'Pre-defined'),
	(12, 'FDA', 'Research Sponsors', 'Pre-defined');
/*!40000 ALTER TABLE `reference_tables` ENABLE KEYS */;

-- Dumping data for table fda_hphc.roles: ~3 rows (approximately)
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` (`role_id`, `role_name`) VALUES
	(1, 'Project Lead'),
	(2, 'Coordinator');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;

-- Dumping data for table fda_hphc.statistic_master_images: ~9 rows (approximately)
/*!40000 ALTER TABLE `statistic_master_images` DISABLE KEYS */;
INSERT INTO `statistic_master_images` (`statistic_image_id`, `value`) VALUES
	(1, 'Activity'),
	(2, 'Sleep'),
	(3, 'Weight'),
	(4, 'Nutrition'),
	(5, 'Heart Rate'),
	(6, 'Blood Glucose'),
	(7, 'Active Task'),
	(8, 'Baby Kicks'),
	(9, 'Other');
/*!40000 ALTER TABLE `statistic_master_images` ENABLE KEYS */;

-- Dumping data for table fda_hphc.users: ~23 rows (approximately)
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` (`user_id`, `first_name`, `last_name`, `email`, `password`, `phone_number`, `role_id`, `created_by`, `created_date`, `modified_by`, `modified_date`, `status`, `access_code`, `accountNonExpired`, `accountNonLocked`, `created_date_time`, `credentialsNonExpired`, `modified_date_time`, `password_expairded_datetime`, `security_token`, `token_expiry_date`, `token_used`, `force_logout`, `user_login_datetime`, `email_changed`) VALUES
	(1, 'Account', 'Manager', 'superadmin@gmail.com', '$2a$10$u9g0amp4vMlEZrfnfH/hBeqXZx9psguQeMb4nzIn798MF2/L51HTi', '333-333-3355', 1, 1, '2018-01-18 14:36:41', 47, '2018-01-18 15:42:55', 1, 'ja67Ll', 1, 1, NULL, 1, NULL, '2020-12-09 10:32:48', 'N8K7zYrc0F', '2020-06-07 19:01:14', 0, 'N', '2020-01-25 19:01:14', 0);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;

-- Dumping data for table fda_hphc.user_permissions: ~7 rows (approximately)
/*!40000 ALTER TABLE `user_permissions` DISABLE KEYS */;
INSERT INTO `user_permissions` (`permission_id`, `permissions`) VALUES
	(8, 'ROLE_CREATE_MANAGE_STUDIES'),
	(6, 'ROLE_MANAGE_APP_WIDE_NOTIFICATION_EDIT'),
	(4, 'ROLE_MANAGE_APP_WIDE_NOTIFICATION_VIEW'),
	(2, 'ROLE_MANAGE_STUDIES'),
	(5, 'ROLE_MANAGE_USERS_EDIT'),
	(7, 'ROLE_MANAGE_USERS_VIEW'),
	(1, 'ROLE_SUPERADMIN');
/*!40000 ALTER TABLE `user_permissions` ENABLE KEYS */;

-- Dumping data for table fda_hphc.user_permission_mapping: ~87 rows (approximately)
/*!40000 ALTER TABLE `user_permission_mapping` DISABLE KEYS */;
INSERT INTO `user_permission_mapping` (`user_id`, `permission_id`) VALUES
	(1, 1),
	(1, 2),
	(1, 4),
	(1, 5),
	(1, 6),
	(1, 7),
	(1, 8);
/*!40000 ALTER TABLE `user_permission_mapping` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
