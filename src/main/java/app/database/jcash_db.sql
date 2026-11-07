-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Nov 07, 2026 at 04:24 AM
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
(47, NULL, 1, 100.00, 'Cash in of user 1', 'Cash in', '', '2026-11-06 10:02:02', '2026-11-06 10:02:02'),
(48, 1, 2, 121.00, 'Transfer from 09876543212 to 09945646355', 'Send Money', 'hello world', '2026-11-06 10:02:10', '2026-11-06 10:02:10'),
(49, NULL, 1, 750.00, 'Cash in of user 1', 'Cash in', '', '2026-11-06 10:02:27', '2026-11-06 10:02:27'),
(50, NULL, 1, 250.00, 'Cash in of user 1', 'Cash in', '', '2026-11-06 10:02:36', '2026-11-06 10:02:36'),
(51, 1, 3, 175.00, 'Transfer from 09876543212 to 09123456789', 'Send Money', 'nam', '2026-11-06 10:03:10', '2026-11-06 10:03:10'),
(52, NULL, 1, 100.00, 'Cash in of user 1', 'Cash in', '', '2026-11-06 18:47:08', '2026-11-06 18:47:08'),
(53, 1, 2, 121.00, 'Transfer from 09876543212 to 09945646355', 'Send Money', 'hello world', '2026-11-06 18:47:18', '2026-11-06 18:47:18'),
(54, NULL, 1, 100.00, 'Cash in of user 1', 'Cash in', '', '2026-11-07 11:09:47', '2026-11-07 11:09:47'),
(55, 1, 2, 121.00, 'Transfer from 09876543212 to 09945646355', 'Send Money', 'hello world', '2026-11-07 11:09:53', '2026-11-07 11:09:53');

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
  `created_at` datetime NOT NULL
) ;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `mobile_number`, `pin`, `first_name`, `last_name`, `email`, `balance`, `created_at`) VALUES
(1, '09876543212', '481f6cc0511143ccdd7e2d1b1b94faf0a700a8b49cd13922a70b5ae28acaa8c5', 'jason', 'begornia', 'jasonbegornia57@gmail.com', 2482.00, '2026-10-31 12:44:01'),
(2, '09945646355', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'tung tung', 'sahur', 'tralala@gmail.com', 2910.00, '2026-10-31 12:45:22'),
(3, '09123456789', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'tralalero', 'tralala', 'tralala26f@gmail.com', 473.00, '2026-11-04 15:57:57'),
(18, '09876543217', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'tralalero', 'tralala', 'tralalero26f@email.com', 0.00, '2026-11-07 11:10:24');

--
-- Indexes for dumped tables
--

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
-- Constraints for table `transactions`
--
ALTER TABLE `transactions`
  ADD CONSTRAINT `fk_transactions_receiver` FOREIGN KEY (`receiver_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `fk_transactions_sender` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
