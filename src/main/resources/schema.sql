CREATE TABLE IF NOT EXISTS teacher(
    id INT AUTO_INCREMENT PRMARY KEY,
    name VARCHAR(100) NOT NULL,
    contact VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS course(
id INT AUTO_INCREMENT PRMARY KEY,
    name VARCHAR(100) NOT NULL,
    duration_in_month INT NOT NULL
);


CREATE TABLE IF NOT EXISTS teacher_course(
    teacher_id INT NOT NULL,
    course_id INT NOT NULL,
    CONTRAINTS PRIMARY KEY(teache_id,course_id),
    CONSTRAINTS fk_1 FORIEGN KEY (teache_id) REFERENCES teacher(id),
    CONSTRAINTS fk_2 FORIEGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE,
)