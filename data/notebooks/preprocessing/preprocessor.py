import mysql.connector
from bs4 import BeautifulSoup
import requests
from time import sleep


### THIS WORKS ONLY WITH DATABASE
# Fix missing and unknown categories for apps as well as undefined application names
def fix_category_data(db):
    play_store_link = 'https://play.google.com/store/apps/details?id={}'
    cursor = db.cursor(named_tuple=True, buffered=True)
    query = f'SELECT * FROM {db_categories}'
    cursor.execute(query)
    unknown_apps = list()

    # data looks like this below
    # Row(app_name='Ziffit', category='UNDEFINED', app_package='uk.co.brightec.ziffit')
    for row in cursor.fetchall():
        pkg = row.app_package
        if row.category in ['UNDEFINED', None]:
            app_page = requests.get(
                play_store_link.format(pkg))  # 404 on this means it doesn't exist on google app store
            sleep(0.5)  # put in to avoid google rate limiting and preventing scrapes
            if app_page.status_code >= 400:
                unknown_apps.append(row)
                continue

            soup = BeautifulSoup(app_page.text, 'lxml')
            genre = str(soup.find('a', itemprop='genre').next_element)
            if genre is None:  # occasionally there was a missing genre for whatever reason (rate limit protection)
                unknown_apps.append(row)

            update_query = (f'REPLACE INTO {db_categories}'
                            f'(app_name, category, app_package)'
                            f'VALUES (%s, %s, %s)')

            cursor.execute(update_query, (row.app_name, genre, row.app_package))
            db.commit()

    cursor.close()
    #     print the unknown apps to text file, to track which ones were manually added
    with open('unknown_apps.txt', 'a+') as unknown:
        for row in unknown_apps: unknown.write(str(row) + '\n')


if __name__ == '__main__':
    db_categories = 'app_categories'
    db_calls = 'calls'
    db_loc = 'locations'
    db_user = 'users'
    db_session = 'user_session_data'

    connector = mysql.connector.connect(user='admin', password='password',
                                        host='127.0.0.1', database='Dissertation',
                                        use_pure=True)

    fix_category_data(connector)

    connector.close()
