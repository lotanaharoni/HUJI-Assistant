package com.example.huji_assistant;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Pair;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LocalDataBase {

    private final ArrayList<StudentInfo> listOfStudents = new ArrayList<>();
    private final Context context;
    private final SharedPreferences sp;
    private final MutableLiveData<List<StudentInfo>> mutableLiveData = new MutableLiveData<>();
    public final LiveData<List<StudentInfo>> publicLiveData = mutableLiveData;
    private ArrayList<Course> listOfCourses = new ArrayList<>();
    private HashMap<String, String> faculty_values_names_map;
    public HashMap<String, String> ruach_faculty_values_names_map;
    private String[] faculty_values;
    public HashMap<String, Chug> chugs = new HashMap<>();


    @RequiresApi(api = Build.VERSION_CODES.N)
    public LocalDataBase(Context context){
        this.context = context;
        this.sp = context.getSharedPreferences("local_db_calculation_items", Context.MODE_PRIVATE);
        faculty_values = new String[]{"0","1","2","3","4","5","6","7","8","9","10","11","12"};
        ruach_faculty_values_names_map = new HashMap<>();
        //Pair<String, String> faculty_pair;

        ruach_faculty_values_names_map.put("כל החוגים", "0");
        ruach_faculty_values_names_map.put("אמירים - תכנית מצטיינים", "0255");
/**
        ruach_faculty_values_names_map.put("0176" >אנגלית
        ruach_faculty_values_names_map.put("0150" >ארכאולוגיה והמזרח הקרוב הקדום
        ruach_faculty_values_names_map.put("0195" >ביה"ס לאמנויות
        ruach_faculty_values_names_map.put("0145" >ביה"ס להגות ודתות
        ruach_faculty_values_names_map.put("0158" >ביה"ס להיסטוריה - תכנית מצטיינים
        ruach_faculty_values_names_map.put("0114" >ביה"ס למדעי הלשון
        ruach_faculty_values_names_map.put("0118" label="ביה&quot;ס לספרויות עתיקות וחדשות ">ביה"ס לספרויות עתיקות וחדשות
        ruach_faculty_values_names_map.put("0249" label="בית הספר לחינוך- תכנית מיוחדת ">בית הספר לחינוך- תכנית מיוחדת
        ruach_faculty_values_names_map.put("0181" label="בלשנות ">בלשנות
        ruach_faculty_values_names_map.put("0192" label="היחידה ללימודי שפות ">היחידה ללימודי שפות
        ruach_faculty_values_names_map.put("0151" label="היסטוריה ">היסטוריה
        ruach_faculty_values_names_map.put("0105" label="היסטוריה של עם ישראל ויהדות זמננו ">היסטוריה של עם ישראל ויהדות זמננו
        ruach_faculty_values_names_map.put("0142" label="היסטוריה, פילוסופיה וסוציולוגיה של המדעים ">היסטוריה, פילוסופיה וסוציולוגיה של המדעים
        ruach_faculty_values_names_map.put("0109" label="הלשון העברית ">הלשון העברית
        ruach_faculty_values_names_map.put("0200" label="חינוך ">חינוך
        ruach_faculty_values_names_map.put("0240" label="חינוך - המגמה לייעוץ חינוכי ">חינוך - המגמה לייעוץ חינוכי
        ruach_faculty_values_names_map.put("0241" label="חינוך - חינוך מיוחד ">חינוך - חינוך מיוחד
        ruach_faculty_values_names_map.put("0242" label="חינוך - למידה והוראה ">חינוך - למידה והוראה
        ruach_faculty_values_names_map.put("0243" label="חינוך - לקויות למידה ">חינוך - לקויות למידה
        ruach_faculty_values_names_map.put("0231" label="חינוך - מחשבת החינוך ">חינוך - מחשבת החינוך
        ruach_faculty_values_names_map.put("0237" label="חינוך - מינהל, מדיניות ומנהיגות בחינוך ">חינוך - מינהל, מדיניות ומנהיגות בחינוך
       ruach_faculty_values_names_map.put("0232" label="חינוך - סוציולוגיה של החינוך ">חינוך - סוציולוגיה של החינוך
       ruach_faculty_values_names_map.put("0238" label="חינוך יהודי ">חינוך יהודי
       ruach_faculty_values_names_map.put("0117" label="חקר פולקלור ותרבות עממית ">חקר פולקלור ותרבות עממית
       ruach_faculty_values_names_map.put("0112" label="יידיש ">יידיש
       ruach_faculty_values_names_map.put("0246" label="ייעוץ חינוכי לגיל הרך ">ייעוץ חינוכי לגיל הרך
       ruach_faculty_values_names_map.put("0124" label="לימודי אסיה ">לימודי אסיה
       ruach_faculty_values_names_map.put("0122" label="לימודי האסלאם והמזרח התיכון ">לימודי האסלאם והמזרח התיכון
       ruach_faculty_values_names_map.put("0201" label="לימודי הוראה - תעודת הוראה ">לימודי הוראה - תעודת הוראה
       ruach_faculty_values_names_map.put("0101" label="לימודי מקרא ">לימודי מקרא
       ruach_faculty_values_names_map.put("0160" label="לימודים גרמניים, רוסיים ומזרח אירופיים ">לימודים גרמניים, רוסיים ומזרח אירופיים
       ruach_faculty_values_names_map.put("0230" label="לימודים משולבים במדעי החינוך ">לימודים משולבים במדעי החינוך
       ruach_faculty_values_names_map.put("0155" label="לימודים ספרדיים ולטינו-אמריקניים ">לימודים ספרדיים ולטינו-אמריקניים
       ruach_faculty_values_names_map.put("0172" label="לימודים קלאסיים ">לימודים קלאסיים
       ruach_faculty_values_names_map.put("0179" label="לימודים רומאניים ">לימודים רומאניים
       ruach_faculty_values_names_map.put("0156" label="לימודים רוסיים וסלאוויים ">לימודים רוסיים וסלאוויים
       ruach_faculty_values_names_map.put("0141" label="מדע הדתות ">מדע הדתות
       ruach_faculty_values_names_map.put("0311" label="מדע המדינה  (מדעי החברה) ">מדע המדינה  (מדעי החברה)
       ruach_faculty_values_names_map.put("0115" label="מדעי היהדות ">מדעי היהדות
       ruach_faculty_values_names_map.put("0143" label="מדעי הקוגניציה והמוח ">מדעי הקוגניציה והמוח
       ruach_faculty_values_names_map.put("0298" label="מדעי הרוח - תכנית אישית למוסמך ">מדעי הרוח - תכנית אישית למוסמך
       ruach_faculty_values_names_map.put("0199" label="מוסיקולוגיה ">מוסיקולוגיה
       ruach_faculty_values_names_map.put("0113" label="מחשבת ישראל ">מחשבת ישראל
       ruach_faculty_values_names_map.put("0301" label="סוציולוגיה ואנתרופולוגיה  (מדעי החברה) ">סוציולוגיה ואנתרופולוגיה  (מדעי החברה)
       ruach_faculty_values_names_map.put("0180" label="ספרות כללית והשוואתית ">ספרות כללית והשוואתית
       ruach_faculty_values_names_map.put("0108" label="ספרות עברית ">ספרות עברית
       ruach_faculty_values_names_map.put("0140" label="פילוסופיה ">פילוסופיה
       ruach_faculty_values_names_map.put("0300" label="פסיכולוגיה  (מדעי החברה) ">פסיכולוגיה  (מדעי החברה)
       ruach_faculty_values_names_map.put("0244" label="פסיכולוגיה חינוכית - תכנית אישית ">פסיכולוגיה חינוכית - תכנית אישית
       ruach_faculty_values_names_map.put("0239" label="פסיכולוגיה חינוכית וקלינית של הילד ">פסיכולוגיה חינוכית וקלינית של הילד
       ruach_faculty_values_names_map.put("0699" label="רפואה - לימודים כלליים  (רפואה) ">רפואה - לימודים כלליים  (רפואה)
       ruach_faculty_values_names_map.put("0157" label="שפה וספרות גרמנית ">שפה וספרות גרמנית
       ruach_faculty_values_names_map.put("0123" label="שפה וספרות ערבית ">שפה וספרות ערבית
       ruach_faculty_values_names_map.put("0222" label="תוכנית הרב תחומית במדעי הרוח ">תוכנית הרב תחומית במדעי הרוח
       ruach_faculty_values_names_map.put("0197" label="תולדות האמנות ">תולדות האמנות
       ruach_faculty_values_names_map.put("0198" label="תולדות התאטרון ">תולדות התאטרון
       ruach_faculty_values_names_map.put("0202" label="תכנית &quot;רביבים&quot; ">תכנית "רביבים"
       ruach_faculty_values_names_map.put("0144" label="תכנית אישית למוסמך במדעי הקוגניציה ">תכנית אישית למוסמך במדעי הקוגניציה
       ruach_faculty_values_names_map.put("0102" label="תלמוד והלכה ">תלמוד והלכה
       ruach_faculty_values_names_map.put("1040" label="הדגש במדעי הרוח הדיגיטליים">הדגש במדעי הרוח הדיגיטליים
       ruach_faculty_values_names_map.put("1767" label="חטיבה באנגלית">חטיבה באנגלית
       ruach_faculty_values_names_map.put("1506" label="חטיבה בארכאולוגיה">חטיבה בארכאולוגיה
       ruach_faculty_values_names_map.put("1811" label="חטיבה בבלשנות">חטיבה בבלשנות
       ruach_faculty_values_names_map.put("1511" label="חטיבה בהיסטוריה">חטיבה בהיסטוריה
       ruach_faculty_values_names_map.put("1057" label="חטיבה בהיסטוריה של עם ישראל">חטיבה בהיסטוריה של עם ישראל
       ruach_faculty_values_names_map.put("1223" label="חטיבה בלימודי האסלאם והמזרח התיכון">חטיבה בלימודי האסלאם והמזרח התיכון
       ruach_faculty_values_names_map.put("1515" label="חטיבה בלימודי ירושלים בתוכנית הרב תחומית">חטיבה בלימודי ירושלים בתוכנית הרב תחומית
       ruach_faculty_values_names_map.put("1411" label="חטיבה בלימודי נצרות">חטיבה בלימודי נצרות
       ruach_faculty_values_names_map.put("1561" label="חטיבה בלימודים רוסיים ומזרח אירופיים">חטיבה בלימודים רוסיים ומזרח אירופיים
       ruach_faculty_values_names_map.put("1790" label="חטיבה בלשונות היהודים וספרויותיהן">חטיבה בלשונות היהודים וספרויותיהן
       ruach_faculty_values_names_map.put("2990" label="חטיבה במדעי הקוגניציה-מדעי הרוח">חטיבה במדעי הקוגניציה-מדעי הרוח
       ruach_faculty_values_names_map.put("1136" label="חטיבה במחשבת ישראל">חטיבה במחשבת ישראל
       ruach_faculty_values_names_map.put("1802" label="חטיבה בספרות השוואתית">חטיבה בספרות השוואתית
       ruach_faculty_values_names_map.put("1084" label="חטיבה בספרות עברית">חטיבה בספרות עברית
       ruach_faculty_values_names_map.put("1401" חטיבה בפילוספיה
       ruach_faculty_values_names_map.put("1971" >חטיבה בתולדות האמנות
       ruach_faculty_values_names_map.put("1248" >חטיבות בלימודי אסיה
       ruach_faculty_values_names_map.put("1016" >חטיבות במקרא
       ruach_faculty_values_names_map.put("1027" label="חטיבות בתלמוד והלכה">חטיבות בתלמוד והלכה
       ruach_faculty_values_names_map.put("2995" label="לימודים משלימים">לימודים משלימים
       ruach_faculty_values_names_map.put("2885" label="קורסי מיומנויות וקורסי שער">קורסי מיומנויות וקורסי שער
       ruach_faculty_values_names_map.put("1069" label="תכנית למסטרנטים מצטיינים">תכנית למסטרנטים מצטיינים

*/

        // initializeSp();
    }

    public HashMap<String, String> getRuach_faculty_values_names_map(){
        return ruach_faculty_values_names_map;
    }
}
