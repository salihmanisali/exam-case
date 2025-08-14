-- Kullanıcı Ekle
-- Username: testuser | Parola: password
INSERT INTO User (username, passwordHash) VALUES ('testuser', '$2a$12$KM551OjYC4lRX7zM9WkGt.RZxqW.OfcGgjPDprPGkOck5o5E92TkC');

-- Sınav Ekle
INSERT INTO Exam (id, title, description) VALUES (1, 'Genel Kültür Sınavı', 'Genel kültür bilginizi test eden basit bir sınav.');

-- Soruları Ekle (Sınav ID: 1)
-- Soru 1: Çoktan Seçmeli
INSERT INTO Question (id, exam_id, text, type) VALUES (1, 1, 'Türkiye\'nin başkenti neresidir?', 'multiple_choice');
INSERT INTO question_options (question_id, option_value) VALUES (1, 'İstanbul');
INSERT INTO question_options (question_id, option_value) VALUES (1, 'Ankara');
INSERT INTO question_options (question_id, option_value) VALUES (1, 'İzmir');

-- Soru 2: Çoktan Seçmeli
INSERT INTO Question (id, exam_id, text, type) VALUES (2, 1, '2 + 2 kaçtır?', 'multiple_choice');
INSERT INTO question_options (question_id, option_value) VALUES (2, '3');
INSERT INTO question_options (question_id, option_value) VALUES (2, '4');
INSERT INTO question_options (question_id, option_value) VALUES (2, '5');

-- Soru 3: Klasik
INSERT INTO Question (id, exam_id, text, type) VALUES (3, 1, 'Nesne Yönelimli Programlama (OOP) kavramını açıklayınız.', 'classical');

-- Başka bir sınav ekleyebilirsiniz
INSERT INTO Exam (id, title, description) VALUES (2, 'Matematik Sınavı', 'Temel matematik becerilerini ölçen bir sınav.');
INSERT INTO Question (id, exam_id, text, type) VALUES (4, 2, '5 * 8 kaçtır?', 'multiple_choice');
INSERT INTO question_options (question_id, option_value) VALUES (4, '35');
INSERT INTO question_options (question_id, option_value) VALUES (4, '40');
INSERT INTO question_options (question_id, option_value) VALUES (4, '45');
