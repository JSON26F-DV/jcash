-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Nov 08, 2026 at 02:20 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `jcash_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `cards`
--

CREATE TABLE `cards` (
  `id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `card_type` enum('debit','credit','prepaid') NOT NULL,
  `color` enum('blue','graphite','onyx','emerald') NOT NULL DEFAULT 'blue',
  `card_number` varchar(19) NOT NULL,
  `expiry_date` char(5) NOT NULL,
  `pin_hash` varchar(255) NOT NULL,
  `is_frozen` tinyint(1) NOT NULL DEFAULT 0,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `cards`
--

INSERT INTO `cards` (`id`, `user_id`, `card_type`, `color`, `card_number`, `expiry_date`, `pin_hash`, `is_frozen`, `created_at`) VALUES
(5, 21, 'debit', 'onyx', '5706 2320 1240 6392', '11/31', '$2a$10$qyMU4wOBWV/nTe/6moGqi.kOvhR.s9jSKVyWg3DzbKmrLsCV9GcZO', 0, '2026-11-08 10:14:05'),
(6, 21, 'credit', 'emerald', '6101 2415 6930 0879', '11/31', '$2a$10$bu7dD84mI7E5Ylhlzd57SO3SQ1GH3lMx329jg9ChdrnWTydcqQPWm', 0, '2026-11-08 10:15:16'),
(7, 22, 'prepaid', 'graphite', '5328 7688 0938 8328', '11/31', '$2a$10$LrUuAgJVt1J.hCR1A8kPZ.UV9xM/8OopdwRzc.TK4WL2sMZjvl.LC', 0, '2026-11-08 11:08:03'),
(8, 21, 'prepaid', 'blue', '7886 4273 1342 8120', '11/31', '$2a$10$ux4Tpga/BfM7g9b/yAPcZeIfgJQGXTwSNB.sbIEmigFLXwhsqEWc.', 0, '2026-11-08 13:09:26');

-- --------------------------------------------------------

--
-- Table structure for table `transactions`
--

CREATE TABLE `transactions` (
  `id` bigint(20) NOT NULL,
  `sender_id` bigint(20) DEFAULT NULL,
  `receiver_id` bigint(20) DEFAULT NULL,
  `amount` decimal(15,2) NOT NULL,
  `description` varchar(150) NOT NULL,
  `category` varchar(50) NOT NULL DEFAULT 'Automatic',
  `message` varchar(100) NOT NULL,
  `created_at` datetime NOT NULL DEFAULT current_timestamp(),
  `updated_at` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ;

--
-- Dumping data for table `transactions`
--

INSERT INTO `transactions` (`id`, `sender_id`, `receiver_id`, `amount`, `description`, `category`, `message`, `created_at`, `updated_at`) VALUES
(60, NULL, 21, 650.00, 'Cash in of user 21', 'Cash in', '', '2026-11-08 18:13:45', '2026-11-08 18:13:45'),
(61, 21, NULL, 250.00, 'Card creation fee for debit card', 'Card fee', 'New card creation fee', '2026-11-08 18:14:05', '2026-11-08 18:14:05'),
(62, 21, 20, 100.00, 'Transfer from 09876543212 to 09876543217', 'Send Money', 'hello world', '2026-11-08 18:14:44', '2026-11-08 18:14:44'),
(63, NULL, 21, 121.00, 'Cash in of user 21', 'Cash in', '', '2026-11-08 18:15:00', '2026-11-08 18:15:00'),
(64, NULL, 21, 320.00, 'Cash in of user 21', 'Cash in', '', '2026-11-08 18:15:07', '2026-11-08 18:15:07'),
(65, 21, NULL, 250.00, 'Card creation fee for credit card', 'Card fee', 'New card creation fee', '2026-11-08 18:15:16', '2026-11-08 18:15:16'),
(66, NULL, 22, 100.00, 'Cash in of user 22', 'Cash in', '', '2026-11-08 19:07:09', '2026-11-08 19:07:09'),
(67, 22, 20, 58.00, 'Transfer from 09945646355 to 09876543217', 'Send Money', 'hello jason', '2026-11-08 19:07:40', '2026-11-08 19:07:40'),
(68, NULL, 22, 500.00, 'Cash in of user 22', 'Cash in', '', '2026-11-08 19:07:50', '2026-11-08 19:07:50'),
(69, 22, NULL, 250.00, 'Card creation fee for prepaid card', 'Card fee', 'New card creation fee', '2026-11-08 19:08:03', '2026-11-08 19:08:03'),
(70, NULL, 21, 121.00, 'Cash in of user 21', 'Cash in', '', '2026-11-08 20:21:15', '2026-11-08 20:21:15'),
(71, 21, 22, 121.00, 'Transfer from 09876543212 to 09945646355', 'Send Money', 'Hello World', '2026-11-08 20:23:11', '2026-11-08 20:23:11'),
(72, 21, NULL, 250.00, 'Card creation fee for prepaid card', 'Card fee', 'New card creation fee', '2026-11-08 21:09:26', '2026-11-08 21:09:26');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` bigint(20) NOT NULL,
  `mobile_number` varchar(11) NOT NULL,
  `pin` varchar(255) NOT NULL,
  `first_name` varchar(100) NOT NULL,
  `last_name` varchar(200) NOT NULL,
  `email` varchar(200) NOT NULL,
  `balance` decimal(15,2) NOT NULL DEFAULT 0.00,
  `created_at` datetime NOT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT 0
) ;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `mobile_number`, `pin`, `first_name`, `last_name`, `email`, `balance`, `created_at`, `is_deleted`) VALUES
(20, '09876543217', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'tralalero', 'tralala', 'tralalero26f@email.com', 158.00, '2026-11-08 18:12:33', 0),
(21, '09876543212', '481f6cc0511143ccdd7e2d1b1b94faf0a700a8b49cd13922a70b5ae28acaa8c5', 'jason', 'begornia', 'jasonbegornia57@gmail.com', 241.00, '2026-11-08 18:13:20', 0),
(22, '09945646355', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'tung tung', 'sahur', 'triplet@gmail.com', 413.00, '2026-11-08 19:03:01', 0),
(23, '09123456789', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'ballerina', 'cappuccina', 'ballerinacappuccina57@gmail.com', 0.00, '2026-11-08 20:05:33', 1);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `cards`
--
ALTER TABLE `cards`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uq_cards_card_number` (`card_number`),
  ADD KEY `idx_cards_user_id` (`user_id`);

--
-- Indexes for table `transactions`
--
ALTER TABLE `transactions`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_transactions_sender` (`sender_id`,`created_at`),
  ADD KEY `idx_transactions_receiver` (`receiver_id`,`created_at`),
  ADD KEY `idx_transactions_created_at` (`created_at`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `mobile_number` (`mobile_number`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `cards`
--
ALTER TABLE `cards`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `transactions`
--
ALTER TABLE `transactions`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `cards`
--
ALTER TABLE `cards`
  ADD CONSTRAINT `fk_cards_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `transactions`
--
ALTER TABLE `transactions`
  ADD CONSTRAINT `fk_transactions_receiver` FOREIGN KEY (`receiver_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `fk_transactions_sender` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
