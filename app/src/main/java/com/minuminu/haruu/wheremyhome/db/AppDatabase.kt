package com.minuminu.haruu.wheremyhome.db

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.minuminu.haruu.wheremyhome.db.dao.*
import com.minuminu.haruu.wheremyhome.db.data.*

@Database(
    entities = [HomeInfo::class, Picture::class, QuestionGroup::class, Question::class, Answer::class],
    version = 3
)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        private lateinit var context: Context
        private val database: AppDatabase by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            val MIGRATION_1_2 = object : Migration(1, 2) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    database.execSQL("ALTER TABLE HomeInfo ADD COLUMN thumbnail TEXT")
                }
            }
            val MIGRATION_2_3 = object : Migration(2, 3) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    database.beginTransaction()
                    try {
                        // QuestionGroup 생성
                        Log.d(javaClass.simpleName, "CREATE QuestionGroup")
                        database.execSQL("CREATE TABLE IF NOT EXISTS QuestionGroup (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, description TEXT)")
                        Log.d(javaClass.simpleName, "INSERT QuestionGroup Data")
                        database.execSQL("INSERT INTO QuestionGroup (id, name, description) VALUES (1, 'base', '기본 템플릿')")

                        // Question 생성
                        Log.d(javaClass.simpleName, "CREATE Question")
                        database.execSQL("CREATE TABLE IF NOT EXISTS Question (id INTEGER PRIMARY KEY AUTOINCREMENT, category TEXT NOT NULL, num INTEGER NOT NULL, content TEXT NOT NULL, type TEXT NOT NULL, question_group_id INTEGER)")
                        Log.d(javaClass.simpleName, "INSERT Question Data")
                        database.execSQL("INSERT INTO Question (id, category, num, content, type, question_group_id) VALUES (1, '내부', '1', '햇빛 채광 점수', 'Int', 1)")
                        database.execSQL("INSERT INTO Question (id, category, num, content, type, question_group_id) VALUES (2, '내부', '2', '물 샌 흔적 갯수(-)', 'Int', 1)")
                        database.execSQL("INSERT INTO Question (id, category, num, content, type, question_group_id) VALUES (3, '내부', '3', '천장/벽/장판 곰팡이 갯수(-)', 'Int', 1)")
                        database.execSQL("INSERT INTO Question (id, category, num, content, type, question_group_id) VALUES (4, '내부', '4', '환기구 갯수', 'Int', 1)")
                        database.execSQL("INSERT INTO Question (id, category, num, content, type, question_group_id) VALUES (5, '내부', '5', '외풍차단 여부', 'Boolean', 1)")
                        database.execSQL("INSERT INTO Question (id, category, num, content, type, question_group_id) VALUES (6, '내부', '6', '발코니 여부', 'Boolean', 1)")
                        database.execSQL("INSERT INTO Question (id, category, num, content, type, question_group_id) VALUES (7, '내부', '7', '빨래 건조 공간 여부', 'Boolean', 1)")
                        database.execSQL("INSERT INTO Question (id, category, num, content, type, question_group_id) VALUES (8, '내부', '8', '방 공간 점수', 'Int', 1)")
                        database.execSQL("INSERT INTO Question (id, category, num, content, type, question_group_id) VALUES (9, '시설물', '9', '전등 갯수', 'Int', 1)")
                        database.execSQL("INSERT INTO Question (id, category, num, content, type, question_group_id) VALUES (10, '시설물', '10', '화장실', 'Int', 1)")
                        database.execSQL("INSERT INTO Question (id, category, num, content, type, question_group_id) VALUES (11, '시설물', '11', '- 수도 상태', 'Boolean', 1)")
                        database.execSQL("INSERT INTO Question (id, category, num, content, type, question_group_id) VALUES (12, '시설물', '12', '- 배수 상태', 'Boolean', 1)")
                        database.execSQL("INSERT INTO Question (id, category, num, content, type, question_group_id) VALUES (13, '시설물', '13', '- 청결 점수', 'Int', 1)")
                        database.execSQL("INSERT INTO Question (id, category, num, content, type, question_group_id) VALUES (14, '시설물', '14', '싱크대 여부', 'Boolean', 1)")
                        database.execSQL("INSERT INTO Question (id, category, num, content, type, question_group_id) VALUES (15, '시설물', '15', '- 수도 상태', 'Boolean', 1)")
                        database.execSQL("INSERT INTO Question (id, category, num, content, type, question_group_id) VALUES (16, '시설물', '16', '- 배수 상태', 'Boolean', 1)")
                        database.execSQL("INSERT INTO Question (id, category, num, content, type, question_group_id) VALUES (17, '시설물', '17', '- 수납장 갯수', 'Int', 1)")
                        database.execSQL("INSERT INTO Question (id, category, num, content, type, question_group_id) VALUES (18, '시설물', '18', '세탁기 여부', 'Boolean', 1)")
                        database.execSQL("INSERT INTO Question (id, category, num, content, type, question_group_id) VALUES (19, '시설물', '19', '- 수도 상태', 'Boolean', 1)")
                        database.execSQL("INSERT INTO Question (id, category, num, content, type, question_group_id) VALUES (20, '시설물', '20', '- 배수 상태', 'Boolean', 1)")
                        database.execSQL("INSERT INTO Question (id, category, num, content, type, question_group_id) VALUES (21, '시설물', '21', '콘센트 갯수', 'Int', 1)")
                        database.execSQL("INSERT INTO Question (id, category, num, content, type, question_group_id) VALUES (22, '시설물', '22', '난방 방식(도시가스/전기)', 'Boolean', 1)")
                        database.execSQL("INSERT INTO Question (id, category, num, content, type, question_group_id) VALUES (23, '입지환경', '23', '방충망 설치 여부', 'Boolean', 1)")
                        database.execSQL("INSERT INTO Question (id, category, num, content, type, question_group_id) VALUES (24, '입지환경', '24', '복도(계단X) 여부', 'Boolean', 1)")
                        database.execSQL("INSERT INTO Question (id, category, num, content, type, question_group_id) VALUES (25, '입지환경', '25', '냄새 점수', 'Int', 1)")
                        database.execSQL("INSERT INTO Question (id, category, num, content, type, question_group_id) VALUES (26, '입지환경', '26', '근처 가로등 여부', 'Boolean', 1)")

                        // Answer 생성 및 Qanda -> Answer 데이터 매핑
                        Log.d(javaClass.simpleName, "CREATE Answer")
                        database.execSQL("CREATE TABLE IF NOT EXISTS Answer (id INTEGER PRIMARY KEY AUTOINCREMENT, content TEXT NOT NULL, remark TEXT NOT NULL, question_id INTEGER, home_info_id INTEGER)")
                        Log.d(javaClass.simpleName, "INSERT Answer Data")
                        database.execSQL("INSERT INTO Answer (id, content, remark, question_id, home_info_id) SELECT id, answer, remark, num AS question_id, home_info_id FROM Qanda")

                        // Drop Qanda
                        Log.d(javaClass.simpleName, "DROP Qanda")
                        database.execSQL("DROP TABLE Qanda")

                        database.setTransactionSuccessful()
                    } catch (e: Throwable) {
                        e.printStackTrace()
                    }

                    database.endTransaction()
                }
            }

            Room.databaseBuilder(context, AppDatabase::class.java, "myhome.db")
                .addMigrations(MIGRATION_1_2)
                .addMigrations(MIGRATION_2_3)
                .build()
        }

        fun getDatabase(context: Context): AppDatabase {
            this.context = context.applicationContext
            return database
        }
    }

    abstract fun homeInfoDao(): HomeInfoDao
    abstract fun pictureDao(): PictureDao
    abstract fun questionGroupDao(): QuestionGroupDao
    abstract fun questionDao(): QuestionDao
    abstract fun answerDao(): AnswerDao
}