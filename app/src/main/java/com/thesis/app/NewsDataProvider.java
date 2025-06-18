package com.thesis.app;

import com.thesis.app.models.NewsItem;
import java.util.ArrayList;
import java.util.List;

public class NewsDataProvider {

    // Faculty News Data
    public static List<NewsItem> getFacultyNewsData() {
        List<NewsItem> facultyNews = new ArrayList<>();

        facultyNews.add(new NewsItem(
                "1",
                "New Faculty Research Center Inaugurated",
                "The university has inaugurated a state-of-the-art research center focusing on artificial intelligence and machine learning. The facility will house 20 senior researchers and provide cutting-edge equipment for advanced studies.",
                "research",
                "faculty",
                System.currentTimeMillis() - 86400000, // 1 day ago
                "December 13, 2024 • 2:30 PM"
        ));

        facultyNews.add(new NewsItem(
                "2",
                "Dr. Sarah Johnson Receives Excellence Award",
                "Dr. Sarah Johnson from the Computer Science department has been awarded the National Teaching Excellence Award for her innovative teaching methods and outstanding contribution to student development.",
                "sarah",
                "faculty",
                System.currentTimeMillis() - 172800000, // 2 days ago
                "December 12, 2024 • 10:15 AM"
        ));

        facultyNews.add(new NewsItem(
                "3",
                "Faculty Development Workshop Series Begins",
                "A comprehensive workshop series on modern teaching methodologies and digital education tools has commenced. Over 50 faculty members are participating in this month-long professional development program.",
                "workshop",
                "faculty",
                System.currentTimeMillis() - 259200000, // 3 days ago
                "December 11, 2024 • 4:45 PM"
        ));

        facultyNews.add(new NewsItem(
                "4",
                "Research Collaboration with International Universities",
                "The university has signed MOUs with three international universities for collaborative research projects in biotechnology, renewable energy, and sustainable development.",
                "uni_colob",
                "faculty",
                System.currentTimeMillis() - 345600000, // 4 days ago
                "December 10, 2024 • 11:20 AM"
        ));

        facultyNews.add(new NewsItem(
                "5",
                "New PhD Program in Data Science Approved",
                "The academic council has approved a new PhD program in Data Science starting from the next academic year. The program will focus on advanced analytics, big data, and predictive modeling.",
                "phd",
                "faculty",
                System.currentTimeMillis() - 432000000, // 5 days ago
                "December 9, 2024 • 3:10 PM"
        ));

        return facultyNews;
    }

    // Events Data
    public static List<NewsItem> getEventsData() {
        List<NewsItem> events = new ArrayList<>();

        events.add(new NewsItem(
                "6",
                "Annual Tech Festival 2024 - TechnoVanza",
                "Join us for the biggest tech festival of the year featuring hackathons, coding competitions, tech talks, and startup showcases. Register now for exciting prizes and networking opportunities.",
                "festival",
                "events",
                System.currentTimeMillis() - 43200000, // 12 hours ago
                "December 14, 2024 • 9:00 AM"
        ));

        events.add(new NewsItem(
                "7",
                "Cultural Night - Celebrating Diversity",
                "Experience the rich cultural heritage of our diverse student community. Enjoy traditional music, dance performances, food stalls, and art exhibitions from around the world.",
                "culture",
                "events",
                System.currentTimeMillis() - 129600000, // 1.5 days ago
                "December 13, 2024 • 6:30 PM"
        ));

        events.add(new NewsItem(
                "8",
                "Career Fair 2024 - Industry Meets Academia",
                "Connect with leading companies and explore career opportunities. Over 100 companies will be participating with job openings, internships, and graduate programs.",
                "inauguration",
                "events",
                System.currentTimeMillis() - 216000000, // 2.5 days ago
                "December 12, 2024 • 10:00 AM"
        ));

        events.add(new NewsItem(
                "9",
                "Guest Lecture: Future of Sustainable Technology",
                "Renowned environmentalist Dr. Michael Green will deliver a keynote on sustainable technology solutions and their impact on climate change. Open to all students and faculty.",
                "innovative",
                "events",
                System.currentTimeMillis() - 302400000, // 3.5 days ago
                "December 11, 2024 • 2:00 PM"
        ));

        events.add(new NewsItem(
                "10",
                "Student Innovation Competition 2024",
                "Showcase your innovative ideas and compete for the grand prize of $10,000. Categories include IoT, AI/ML, Mobile Apps, and Social Impact projects. Registration deadline: December 20th.",
                "tech",
                "events",
                System.currentTimeMillis() - 388800000, // 4.5 days ago
                "December 10, 2024 • 1:15 PM"
        ));

        return events;
    }

    // Sports Data
    public static List<NewsItem> getSportsData() {
        List<NewsItem> sports = new ArrayList<>();

        sports.add(new NewsItem(
                "11",
                "University Cricket Team Wins Inter-College Tournament",
                "Our cricket team has emerged victorious in the inter-college tournament, defeating five other universities. The final match was an exciting encounter with a thrilling finish.",
                "cricket",
                "sports",
                System.currentTimeMillis() - 21600000, // 6 hours ago
                "December 14, 2024 • 3:15 PM"
        ));

        sports.add(new NewsItem(
                "12",
                "New Swimming Pool Complex Inaugurated",
                "The university has inaugurated a modern swimming pool complex with Olympic-standard facilities. The complex includes a 50-meter pool, diving pool, and training facilities.",
                "pool",
                "sports",
                System.currentTimeMillis() - 108000000, // 30 hours ago
                "December 13, 2024 • 11:30 AM"
        ));

        sports.add(new NewsItem(
                "13",
                "Basketball Team Qualifies for National Championship",
                "After a series of impressive performances, our basketball team has qualified for the national championship. The team will compete against 16 other universities next month.",
                "basketball",
                "sports",
                System.currentTimeMillis() - 194400000, // 54 hours ago
                "December 12, 2024 • 7:45 PM"
        ));

        sports.add(new NewsItem(
                "14",
                "Annual Sports Day 2024 Registration Open",
                "Registration is now open for Annual Sports Day 2024. Multiple categories including athletics, team sports, and recreational activities. Prizes worth $5,000 to be won.",
                "sportsday",
                "sports",
                System.currentTimeMillis() - 280800000, // 78 hours ago
                "December 11, 2024 • 8:20 AM"
        ));

        sports.add(new NewsItem(
                "15",
                "Fitness Center Upgraded with Modern Equipment",
                "The campus fitness center has been upgraded with state-of-the-art equipment including cardio machines, strength training equipment, and specialized training zones.",
                "gym",
                "sports",
                System.currentTimeMillis() - 367200000, // 102 hours ago
                "December 10, 2024 • 5:40 PM"
        ));

        return sports;
    }
}
